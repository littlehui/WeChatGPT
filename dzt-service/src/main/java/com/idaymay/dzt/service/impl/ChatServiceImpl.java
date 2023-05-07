package com.idaymay.dzt.service.impl;

import cn.hutool.crypto.digest.MD5;
import com.idaymay.dzt.bean.constant.ChatConstants;
import com.idaymay.dzt.bean.dto.QuestionDTO;
import com.idaymay.dzt.bean.openai.OpenAiConfigSupport;
import com.idaymay.dzt.common.exception.AnswerTimeOutException;
import com.idaymay.dzt.common.redission.CustomRedissonLock;
import com.idaymay.dzt.common.redission.NeedRateLimit;
import com.idaymay.dzt.common.utils.string.StringUtil;
import com.idaymay.dzt.dao.redis.domain.AnswerCache;
import com.idaymay.dzt.dao.redis.domain.ChatMessageCache;
import com.idaymay.dzt.dao.redis.domain.QuestionCache;
import com.idaymay.dzt.dao.redis.domain.UserConfigCache;
import com.idaymay.dzt.dao.redis.repository.*;
import com.idaymay.dzt.service.ChatService;
import com.idaymay.dzt.service.MessagePublisher;
import com.unfbx.chatgpt.OpenAiClient;
import com.unfbx.chatgpt.entity.chat.ChatChoice;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import com.unfbx.chatgpt.entity.chat.ChatCompletionResponse;
import com.unfbx.chatgpt.entity.chat.Message;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.Time;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import retrofit2.HttpException;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;

/**
 * TODO
 *
 * @author littlehui
 * @version 1.0
 * @date 2023/04/11 21:26
 */
@Service
@Slf4j
public class ChatServiceImpl implements ChatService, ApplicationContextAware {

    private ApplicationContext context;

    @Autowired
    AnswerCacheRepository answerCacheRepository;

    @Autowired
    QuestionCacheRepository questionCacheRepository;

    @Autowired
    SnowflakeIdGenerator snowflakeIdGenerator;

    @Autowired
    MessagePublisher messagePublisher;

    @Autowired
    OpenAiClient openAiClient;

    @Autowired
    ChatMessageRepository chatMessageRepository;

    OpenAiConfigSupport openAiConfigSupport;

    @Autowired
    UserConfigCacheRepository userConfigCacheRepository;

    @Autowired
    UserKeyStrategy userKeyStrategy;

    @Autowired
    FreeCountCacheRepository freeCountCacheRepository;

    @Autowired
    CurrentAnswerQuestionRepository currentAnswerQuestionRepository;

    @Autowired
    CurrentQuestionCheckRepository currentQuestionCheckRepository;

    ThreadPoolExecutor chatThreadPool = new ThreadPoolExecutor(2, 4, 20, TimeUnit.SECONDS, new ArrayBlockingQueue(1000),
            new ThreadPoolExecutor.CallerRunsPolicy());

    ThreadPoolExecutor answerThreadPool = new ThreadPoolExecutor(2, 4, 20, TimeUnit.SECONDS, new ArrayBlockingQueue(1000),
            new ThreadPoolExecutor.CallerRunsPolicy());

    @NeedRateLimit(limitKey = "#fromUser", permit = 4, timeOut = 10, fromUser = "#fromUser", toUser = "#toUser")
    @CustomRedissonLock(lockIndex = 1, leaseTime = 3)
    @Override
    public String askAQuestion(Long startTime, String question, String fromUser, String toUser) {
        String messageId = ChatConstants.QUESTION_ID_PRE + MD5.create().digestHex16(question + fromUser + toUser).toUpperCase(Locale.ROOT);
        //第几次进行answer
        Integer currentAnswerCount = 1;
        QuestionCache questionCache = questionCacheRepository.getQuestionByMessageId(messageId);
        if (questionCache != null) {
            currentAnswerCount = questionCache.getRequestEdTimes().intValue() + 1;
            //请求次数+1
            questionCacheRepository.saveQuestion(messageId, question, fromUser, questionCache.getRequestEdTimes() + 1);
            //已经提问过了。
            Long currentTimeSeconds = new BigDecimal(System.currentTimeMillis()).multiply(new BigDecimal(1000)).longValue();
            Long waitTimeSeconds = 1L;
            if ((currentTimeSeconds - startTime) < 5) {
                waitTimeSeconds = 5 - (currentTimeSeconds - startTime);
            } else {
                waitTimeSeconds = 5 - (currentTimeSeconds - startTime) % 5;
                if (waitTimeSeconds == 0) {
                    waitTimeSeconds = 1L;
                }
            }
            log.warn("from user:{},messageId:{},当前第{}次请求。开始寻找答案！,等待时间:{}秒", fromUser, messageId, currentAnswerCount, waitTimeSeconds);
            //阻塞查询是否有答案
            Future<Boolean> answered = hasAnswered(waitTimeSeconds, fromUser, messageId, question);
            String answerContent = null;
            try {
                log.info("messageId：{},第{}次", messageId, currentAnswerCount);
                Boolean answerFlag = answered.get();
                if (answerFlag) {
                    return answerAQuestion(fromUser, messageId);
                } else {
                    if (currentAnswerCount.intValue()%ChatConstants.QUESTION_REQUEST_TOTAL_TIMES == 0) {
                        //第3次第6次
                        log.info("messageId:{},第{}次", messageId, ChatConstants.QUESTION_REQUEST_TOTAL_TIMES);
                        log.info("异步等待时间：{} 秒。", waitTimeSeconds);
                        //缓存一下当前正在确认的messageId
                        currentQuestionCheckRepository.setCurrentCheck(fromUser, question);
                        return String.format(ChatConstants.REPEAT_QUESTION, question);
                    } else {
                        //第二次，第4次，第5次，第7次，第8次
                        throw new AnswerTimeOutException(messageId, fromUser, currentAnswerCount);
                    }
                }
            } catch (InterruptedException e) {
                log.warn("等待结果失败：messageId:{}, exception:{}", messageId, e);
            } catch (ExecutionException e) {
                log.warn("等待结果失败：messageId:{}, exception:{}", messageId, e);
            }
            return answerContent;
        } else {
            //本次问题第一次请求
            questionCache = questionCacheRepository.saveQuestion(messageId, question, fromUser, 1);
            answerCacheRepository.initAnswer(messageId, question, fromUser);
            QuestionDTO questionDTO = QuestionDTO.builder()
                    .askTimeMills(questionCache.getAskTimeMills())
                    .messageId(messageId)
                    .question(questionCache.getQuestion())
                    .requestTimes(1)
                    .userCode(fromUser)
                    .build();
            //messagePublisher.publishMessage(questionDTO);
            chat(questionDTO);
            Long currentTime = new BigDecimal(System.currentTimeMillis()).multiply(new BigDecimal(1000)).longValue();
            if ((currentTime - startTime) < 3) {
                //直接返回
                return answerAQuestion(fromUser, messageId);
            } else {
                //抛异常
                throw new AnswerTimeOutException(messageId, questionDTO.getUserCode(), currentAnswerCount);
            }
        }
    }

    private Future<Boolean> hasAnswered(Long waitTimeSeconds, String fromUser, String messageId, String question) {
        Future<Boolean> answered = answerThreadPool.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                //一秒一次
                for (int i = 0; i < waitTimeSeconds; i++) {
                    AnswerCache answerCache = answerCacheRepository.getAnswerByMessageId(messageId);
                    if (answerCache.getAnswerTimeMills() != null) {
                        log.info("getAnswer成功：{} ", messageId);
                        return true;
                    } else {
                        log.info("未获取到回答，暂停1秒");
                        Thread.sleep(1000);
                    }
                }
                return false;
            }
        });
        return answered;
    }

    @Override
    public String answerAQuestion(String userCode, String messageId) {
        AnswerCache answerCache = answerCacheRepository.getAnswerByMessageId(messageId);
        currentAnswerQuestionRepository.setCurrentAnswerMessageId(userCode, messageId);
        String currentAnswerSegment = ChatConstants.DEFAULT_ANSWER;
        if (answerCache != null) {
            if (answerCache.getAnswerTimeMills() != null) {
                //已经回答
                if (answerCache.getAnswerSegment() != null && answerCache.getAnswerSegment().size() > 0) {
                    Integer currentSegment = answerCache.getCurrentSegment();
                    currentAnswerSegment = answerCache.getAnswerSegment().get(currentSegment);
                    answerCacheRepository.incrAnswerCurrentSegment(messageId);
                } else {
                    currentAnswerSegment = answerCache.getAnswer();
                }
            }
        }
        return currentAnswerSegment;
    }

    @Override
    public String chat(QuestionDTO questionDTO, Long associationRound) {
        Future<String> content = chatThreadPool.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                ChatCompletion chatCompletion = new ChatCompletion();
                chatCompletion.setMessages(makeChatMessages(questionDTO, associationRound));
                userKeyStrategy.setUserCode(questionDTO.getUserCode());
                ChatCompletionResponse response = openAiClient.chatCompletion(chatCompletion);
                //试用次数+1
                UserConfigCache userConfigCache = userConfigCacheRepository.getUserConfig(questionDTO.getUserCode());
                if (userConfigCache == null || userConfigCache.getOpenAiApiKey() == null) {
                    freeCountCacheRepository.incrUsedFreeCount(questionDTO.getUserCode());
                }
                log.info("chat response:{}", response);
                Message answerMessage = completionAnswer(response);
                saveChatMessageToCache(questionDTO, answerMessage);
                log.info("question {}, answered!", questionDTO.getMessageId());
                return answerMessage.getContent();
                //return answerAQuestion(questionDTO.getUserCode(), questionDTO.getMessageId());
            }
        });
        try {
            return content.get(ChatConstants.CHAT_PROCESS_TIME_OUT, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("{}", e);
        } catch (ExecutionException e) {
            if (e.getCause() != null && e.getCause() instanceof HttpException) {
                int httpCode = ((HttpException) e.getCause()).code();
                switch (httpCode) {
                    case 400:
                        log.warn("请求到openapi发生错误，可能是http 400,exception:{}", ((HttpException) e.getCause()).message());
                        log.warn("上下文太多自动去掉一个重试！");
                        if (openAiConfigSupport.getAssociationRound() > 1) {
                            return chat(questionDTO, openAiConfigSupport.getAssociationRound() - 1);
                        } else {
                            saveAnswerMessageToCache(questionDTO, ChatConstants.ANSWER_ERROR);
                            return ChatConstants.ANSWER_ERROR;
                        }
                    case 401:
                        log.warn("请求到openapi发生错误，:{}", ((HttpException) e.getCause()).message());
                        saveAnswerMessageToCache(questionDTO, ChatConstants.API_KEY_ERROR);
                        return ChatConstants.API_KEY_ERROR;
                    default:
                        log.warn("请求到openapi发生错误:{}", ((HttpException) e.getCause()).message());
                }
            }
            log.error("{}", e);
        } catch (TimeoutException e) {
            log.warn("回答超时，直接返回。{}", e.getMessage());
            //丢弃 等下次重试的返回
            throw new AnswerTimeOutException(questionDTO.getMessageId(), questionDTO.getUserCode(), 1);
        }
        return ChatConstants.THINKING;
    }

    @Override
    public String chat(QuestionDTO questionDTO) {
        OpenAiConfigSupport openAiConfigSupport = context.getBean(OpenAiConfigSupport.class);
        Long associationRound = openAiConfigSupport.getAssociationRound();
        return chat(questionDTO, associationRound);
    }

    @Override
    public String continueAnswer(String userCode) {
        String messageId = currentAnswerQuestionRepository.getMessageId(userCode);
        if (messageId == null) {
            return ChatConstants.DEFAULT_ANSWER;
        }
        return answerAQuestion(userCode, messageId);
    }

    private Message completionAnswer(ChatCompletionResponse response) {
        Message answerMessage = Message.builder()
                .role(Message.Role.ASSISTANT)
                .content(ChatConstants.THINKING)
                .build();
        if (response != null && response.getChoices() != null && response.getChoices().size() > 0) {
            ChatChoice choice = response.getChoices().get(0);
            log.info("choice != null");
            if (choice.getMessage() != null) {
                answerMessage = choice.getMessage();
            } else {
                log.warn("answer message is null");
            }
        } else {
            log.info("choice is null");
        }
        return answerMessage;
    }

    private List<Message> makeChatMessages(QuestionDTO questionDTO, Long associationRound) {
        Set<ChatMessageCache> chatMessageCaches = chatMessageRepository.latest(questionDTO.getUserCode(), associationRound * 2);
        List<Message> messages = new ArrayList<>();
        List<Message> historyMessages = new ArrayList<>();
        for (ChatMessageCache chatMessageCache : chatMessageCaches) {
            historyMessages.add(messageCacheToMessage(chatMessageCache));
        }
        Message currentMessage = Message.builder()
                .content(questionDTO.getQuestion())
                .name(questionDTO.getUserCode())
                .role(Message.Role.USER)
                .build();
        messages.addAll(historyMessages);
        messages.add(currentMessage);
        return messages;
    }

    private Message messageCacheToMessage(ChatMessageCache messageCache) {
/*        String content = "";
        if (messageCache.getContent() != null && messageCache.getContent().length() > 100) {
            content = messageCache.getContent().substring(0, 100);
        }*/
        return Message.builder()
                .content(messageCache.getContent())
                .name(messageCache.getName())
                .role(messageCache.getRole() != null
                        ? Message.Role.valueOf(messageCache.getRole().toUpperCase())
                        : null)
                .build();
    }

    private ChatMessageCache messageToMessageCache(Message message) {
        return ChatMessageCache.builder()
                .content(message.getContent())
                .name(message.getName())
                .createTimeMills(System.currentTimeMillis())
                .role(message.getRole())
                .build();
    }

    private void saveAnswerMessageToCache(QuestionDTO questionDTO, String answerContent) {
        AnswerCache answerCache = AnswerCache.builder()
                .answer(answerContent)
                .messageId(questionDTO.getMessageId())
                .question(questionDTO.getQuestion())
                .askTimeMills(questionDTO.getAskTimeMills())
                .answerTimeMills(System.currentTimeMillis())
                .answerSegment(takeAnswerSegment(answerContent, questionDTO.getMessageId()))
                .currentSegment(0)
                .build();
        answerCacheRepository.saveAnswer(answerCache);
        currentAnswerQuestionRepository.setCurrentAnswerMessageId(questionDTO.getUserCode(), questionDTO.getMessageId());
    }

    private List<String> takeAnswerSegment(String answerContent, String messageId) {
        List<String> answerLists = Arrays.asList(StringUtil.foldString(answerContent, 500));
        //TO DO
        String answerSub = String.format(ChatConstants.CONTINUE_SUB, messageId);
        for (int i=0; i<answerLists.size() - 1; i ++) {
            answerLists.set(i, answerLists.get(i) + answerSub);
        }
        return answerLists;
    }

    private AnswerCache saveChatMessageToCache(QuestionDTO questionDTO, Message answerMessage) {
        String answerContent = answerMessage.getContent();
        AnswerCache answerCache = AnswerCache.builder()
                .answer(answerContent)
                .messageId(questionDTO.getMessageId())
                .question(questionDTO.getQuestion())
                .askTimeMills(questionDTO.getAskTimeMills())
                .answerTimeMills(System.currentTimeMillis())
                .name(answerMessage.getName())
                .answerSegment(takeAnswerSegment(answerContent, questionDTO.getMessageId()))
                .currentSegment(0)
                .build();
        ChatMessageCache answerMessageCache = messageToMessageCache(answerMessage);
        ChatMessageCache questionMessageCache = ChatMessageCache.builder()
                .content(questionDTO.getQuestion())
                .name(questionDTO.getUserCode())
                .role(Message.Role.USER.name())
                .createTimeMills(questionDTO.getAskTimeMills())
                .build();
        answerCacheRepository.saveAnswer(answerCache);
        chatMessageRepository.add(questionDTO.getUserCode(), questionMessageCache);
        chatMessageRepository.add(questionDTO.getUserCode(), answerMessageCache);
        currentAnswerQuestionRepository.setCurrentAnswerMessageId(questionDTO.getUserCode(), questionDTO.getMessageId());
        return answerCache;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
        openAiConfigSupport = context.getBean(OpenAiConfigSupport.class);
    }
}
