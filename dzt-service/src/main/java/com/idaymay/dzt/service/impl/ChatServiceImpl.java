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
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

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
    DashScopeUserChatModelFactory dashScopeUserChatModelFactory;

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

    private static final Long TIME_OUT_PERTCOUNT_SECONDS = 4L;

    ThreadPoolExecutor chatThreadPool = new ThreadPoolExecutor(2, 4, 20, TimeUnit.SECONDS, new ArrayBlockingQueue(1000),
            new ThreadPoolExecutor.CallerRunsPolicy());

    ThreadPoolExecutor answerThreadPool = new ThreadPoolExecutor(2, 4, 20, TimeUnit.SECONDS, new ArrayBlockingQueue(1000),
            new ThreadPoolExecutor.CallerRunsPolicy());

    @NeedRateLimit(limitKey = "#fromUser", permit = 4, timeOut = 10, fromUser = "#fromUser", toUser = "#toUser")
    @CustomRedissonLock(lockIndex = 1, leaseTime = 3)
    @Override
    public String askAQuestion(Long startTime, String question, String fromUser, String toUser) {
        String messageId = ChatConstants.QUESTION_ID_PRE + MD5.create().digestHex16( question + fromUser + toUser).toUpperCase(Locale.ROOT);
        //第几次进行answer
        Integer currentAnswerCount = 1;
        QuestionCache questionCache = questionCacheRepository.getQuestionByMessageId(messageId);
        if (questionCache != null) {
            currentAnswerCount = questionCache.getRequestEdTimes().intValue() + 1;
            //请求次数+1
            questionCacheRepository.saveQuestion(messageId, question, fromUser, questionCache.getRequestEdTimes() + 1);
            //已经提问过了。
            Long currentTimeSeconds = new BigDecimal(System.currentTimeMillis()).divide(new BigDecimal(1000)).longValue();
            Long waitTimeSeconds = 1L;
            if ((currentTimeSeconds - startTime) < TIME_OUT_PERTCOUNT_SECONDS) {
                waitTimeSeconds = TIME_OUT_PERTCOUNT_SECONDS - (currentTimeSeconds - startTime);
            } else {
                waitTimeSeconds = TIME_OUT_PERTCOUNT_SECONDS - (currentTimeSeconds - startTime) % TIME_OUT_PERTCOUNT_SECONDS;
                if (waitTimeSeconds == 0) {
                    waitTimeSeconds = 1L;
                }
            }
            log.warn("from user:{},messageId:{},当前第{}次请求。开始寻找答案！,等待时间:{}秒", fromUser, messageId, currentAnswerCount, waitTimeSeconds);
            //阻塞查询是否有答案
            if (waitTimeSeconds > 4) {
                log.error("等待时间超过4秒异常，取4.");
                waitTimeSeconds = TIME_OUT_PERTCOUNT_SECONDS;
            }
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
            chat(questionDTO.getMessageId(), questionDTO);
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
                try {
                    userKeyStrategy.setUserCode(questionDTO.getUserCode());
                    List<Message> springMessages = buildSpringAiMessages(questionDTO, associationRound);
                    Prompt prompt = new Prompt(springMessages);
                    ChatResponse response = dashScopeUserChatModelFactory.call(questionDTO.getUserCode(), prompt);
                    //试用次数+1
                    UserConfigCache userConfigCache = userConfigCacheRepository.getUserConfig(questionDTO.getUserCode());
                    if (userConfigCache == null || userConfigCache.getOpenAiApiKey() == null) {
                        freeCountCacheRepository.incrUsedFreeCount(questionDTO.getUserCode());
                    }
                    log.info("chat response:{}", response);
                    AssistantMessage answerMessage = completionAnswer(response);
                    saveChatMessageToCache(questionDTO, answerMessage);
                    log.info("question {}, answered!", questionDTO.getMessageId());
                    return answerMessage.getText();
                } finally {
                    userKeyStrategy.clearUserCode();
                }
            }
        });
        try {
            return content.get(ChatConstants.CHAT_PROCESS_TIME_OUT, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("{}", e);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            int httpCode = LlmHttpStatusUtil.resolveHttpStatus(cause);
            switch (httpCode) {
                case 400:
                    log.warn("请求到 LLM 网关发生错误，可能是 http 400,exception:{}", cause.toString());
                    log.warn("上下文太多自动去掉一个重试！");
                    if (openAiConfigSupport.getAssociationRound() > 1) {
                        return chat(questionDTO, openAiConfigSupport.getAssociationRound() - 1);
                    } else {
                        saveAnswerMessageToCache(questionDTO, ChatConstants.ANSWER_ERROR);
                        return ChatConstants.ANSWER_ERROR;
                    }
                case 401:
                    log.warn("请求到 LLM 网关发生错误，:{}", cause.toString());
                    saveAnswerMessageToCache(questionDTO, ChatConstants.API_KEY_ERROR);
                    return ChatConstants.API_KEY_ERROR;
                default:
                    if (httpCode > 0) {
                        log.warn("请求到 LLM 网关发生错误:{}", cause.toString());
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

   //只能来一次
   @Override
    public String chat(String messageId, QuestionDTO questionDTO) {
        Long askTimeMills = questionDTO.getAskTimeMills() == null ? System.currentTimeMillis() : questionDTO.getAskTimeMills();
        questionDTO.setAskTimeMills(askTimeMills);
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

    private AssistantMessage completionAnswer(ChatResponse response) {
        AssistantMessage fallback = new AssistantMessage(ChatConstants.THINKING);
        if (response != null && response.getResult() != null && response.getResult().getOutput() != null) {
            return response.getResult().getOutput();
        }
        log.info("choice is null or empty");
        return fallback;
    }

    private List<Message> buildSpringAiMessages(QuestionDTO questionDTO, Long associationRound) {
        Set<ChatMessageCache> chatMessageCaches = chatMessageRepository.latest(questionDTO.getUserCode(), associationRound * 2);
        List<Message> messages = new ArrayList<>();
        List<Message> historyMessages = new ArrayList<>();
        for (ChatMessageCache chatMessageCache : chatMessageCaches) {
            historyMessages.add(chatMessageCacheToSpringMessage(chatMessageCache));
        }
        UserMessage.Builder ub = UserMessage.builder().text(questionDTO.getQuestion());
        if (StringUtil.isNotEmpty(questionDTO.getUserCode())) {
            ub.metadata(Collections.singletonMap("name", questionDTO.getUserCode()));
        }
        Message currentMessage = ub.build();
        messages.addAll(historyMessages);
        messages.add(currentMessage);
        return messages;
    }

    private Message chatMessageCacheToSpringMessage(ChatMessageCache messageCache) {
        String role = messageCache.getRole();
        String content = messageCache.getContent();
        String name = messageCache.getName();
        if (StringUtil.isEmpty(role)) {
            return UserMessage.builder().text(content).build();
        }
        try {
            MessageType type = MessageType.fromValue(role.toLowerCase(Locale.ROOT));
            if (type == MessageType.SYSTEM) {
                return new SystemMessage(content);
            }
            if (type == MessageType.ASSISTANT) {
                Map<String, Object> meta = StringUtil.isNotEmpty(name)
                        ? Collections.singletonMap("name", name)
                        : Collections.emptyMap();
                return new AssistantMessage(content, meta);
            }
        } catch (IllegalArgumentException ignored) {
            // fall through to user
        }
        UserMessage.Builder b = UserMessage.builder().text(content);
        if (StringUtil.isNotEmpty(name)) {
            b.metadata(Collections.singletonMap("name", name));
        }
        return b.build();
    }

    private ChatMessageCache springMessageToMessageCache(Message message) {
        String role = message.getMessageType() != null ? message.getMessageType().getValue() : MessageType.USER.getValue();
        Map<String, Object> meta = message.getMetadata();
        String name = meta != null && meta.get("name") != null ? meta.get("name").toString() : null;
        return ChatMessageCache.builder()
                .content(message.getText())
                .name(name)
                .createTimeMills(System.currentTimeMillis())
                .role(role)
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

    private AnswerCache saveChatMessageToCache(QuestionDTO questionDTO, AssistantMessage answerMessage) {
        String answerContent = answerMessage.getText();
        AnswerCache answerCache = AnswerCache.builder()
                .answer(answerContent)
                .messageId(questionDTO.getMessageId())
                .question(questionDTO.getQuestion())
                .askTimeMills(questionDTO.getAskTimeMills())
                .answerTimeMills(System.currentTimeMillis())
                .name(extractNameFromMetadata(answerMessage))
                .answerSegment(takeAnswerSegment(answerContent, questionDTO.getMessageId()))
                .currentSegment(0)
                .build();
        ChatMessageCache answerMessageCache = springMessageToMessageCache(answerMessage);
        ChatMessageCache questionMessageCache = ChatMessageCache.builder()
                .content(questionDTO.getQuestion())
                .name(questionDTO.getUserCode())
                .role(MessageType.USER.getValue())
                .createTimeMills(questionDTO.getAskTimeMills())
                .build();
        answerCacheRepository.saveAnswer(answerCache);
        chatMessageRepository.add(questionDTO.getUserCode(), questionMessageCache);
        chatMessageRepository.add(questionDTO.getUserCode(), answerMessageCache);
        currentAnswerQuestionRepository.setCurrentAnswerMessageId(questionDTO.getUserCode(), questionDTO.getMessageId());
        return answerCache;
    }

    private static String extractNameFromMetadata(AssistantMessage message) {
        if (message.getMetadata() == null) {
            return null;
        }
        Object n = message.getMetadata().get("name");
        return n != null ? n.toString() : null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
        openAiConfigSupport = context.getBean(OpenAiConfigSupport.class);
    }
}
