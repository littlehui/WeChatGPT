package com.idaymay.dzt.service.impl;

import com.idaymay.dzt.bean.constant.ChatConstants;
import com.idaymay.dzt.bean.dto.QuestionDTO;
import com.idaymay.dzt.bean.openai.OpenAiConfigSupport;
import com.idaymay.dzt.common.utils.string.StringUtil;
import com.idaymay.dzt.dao.redis.domain.AnswerCache;
import com.idaymay.dzt.dao.redis.domain.ChatMessageCache;
import com.idaymay.dzt.dao.redis.domain.QuestionCache;
import com.idaymay.dzt.dao.redis.repository.AnswerCacheRepository;
import com.idaymay.dzt.dao.redis.repository.ChatMessageRepository;
import com.idaymay.dzt.dao.redis.repository.QuestionCacheRepository;
import com.idaymay.dzt.service.ChatService;
import com.idaymay.dzt.service.MessagePublisher;
import com.unfbx.chatgpt.OpenAiClient;
import com.unfbx.chatgpt.entity.chat.ChatChoice;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import com.unfbx.chatgpt.entity.chat.ChatCompletionResponse;
import com.unfbx.chatgpt.entity.chat.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
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

    @Autowired
    OpenAiConfigSupport openAiConfigSupport;

    ThreadPoolExecutor chatThreadPool = new ThreadPoolExecutor(1, 1, 20, TimeUnit.SECONDS, new ArrayBlockingQueue(1000),
            new ThreadPoolExecutor.CallerRunsPolicy());

    @Override
    public String askAQuestion(String question, String user) {
        String messageId = snowflakeIdGenerator.generaMessageId();
        QuestionCache questionCache = questionCacheRepository.saveQuestion(messageId, question, user);
        answerCacheRepository.initAnswer(messageId, question, user);
        QuestionDTO questionDTO = QuestionDTO.builder()
                .askTimeMills(questionCache.getAskTimeMills())
                .messageId(messageId)
                .question(questionCache.getQuestion())
                .user(user)
                .build();
        //messagePublisher.publishMessage(GsonUtil.toJson(questionDTO));
        //String messageQuestion = new String(jackson2JsonRedisSerializer.serialize(questionDTO), StandardCharsets.UTF_8);
        messagePublisher.publishMessage(questionDTO);
        return String.format(ChatConstants.QUICK_ANSWER, messageId);
    }

    @Override
    public String answerAQuestion(String messageId) {
        AnswerCache answerCache = answerCacheRepository.getAnswerByMessageId(messageId);
        String currentAnswerSegment = ChatConstants.DEFAULT_ANSWER;
        if (answerCache != null) {
            if (answerCache.getAnswerTimeMills() != null) {
                //已经回答
                if (answerCache.getAnswerSegment() != null && answerCache.getAnswerSegment().size() > 0) {
                    Integer currentSegment = answerCache.getCurrentSegment();
                    Integer segmentCount = answerCache.getAnswerSegment().size();
                    currentAnswerSegment = answerCache.getAnswerSegment().get(currentSegment);
                    Integer preSegment = currentSegment + 1;
                    if (preSegment >= segmentCount) {
                        preSegment = 0;
                    }
                    answerCache.setCurrentSegment(preSegment);
                }
                //更新一下阅读的分页点
                answerCacheRepository.saveAnswer(answerCache);
            } else {
                currentAnswerSegment = answerCache.getAnswer();
            }
        }
        return currentAnswerSegment;
    }

    @Override
    public String chat(QuestionDTO questionDTO) {
        Future<String> content = chatThreadPool.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                ChatCompletion chatCompletion = new ChatCompletion();
                chatCompletion.setMessages(makeChatMessages(questionDTO));
                ChatCompletionResponse response = openAiClient.chatCompletion(chatCompletion);
                log.info("chat response:{}", response);
                Message answerMessage = completionAnswer(response);
                saveChatMessageToCache(questionDTO, answerMessage);
                log.info("question {}, answered!", questionDTO.getMessageId());
                return answerMessage.getContent();
            }
        });
        try {
            return content.get();
        } catch (InterruptedException e) {
            log.error("{}", e);
        } catch (ExecutionException e) {
            log.error("{}", e);
        }
        return ChatConstants.THINKING;
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

    private List<Message> makeChatMessages(QuestionDTO questionDTO) {
        OpenAiConfigSupport openAiConfigSupport = context.getBean(OpenAiConfigSupport.class);
        Long associationCount = openAiConfigSupport.getAssociationRound();
        Set<ChatMessageCache> chatMessageCaches = chatMessageRepository.top(questionDTO.getUser(), associationCount * 2);
        List<Message> messages = new ArrayList<>();
        List<Message> historyMessages = new ArrayList<>();
        for (ChatMessageCache chatMessageCache : chatMessageCaches) {
            historyMessages.add(messageCacheToMessage(chatMessageCache));
        }
        Message currentMessage = Message.builder()
                .content(questionDTO.getQuestion())
                .name(questionDTO.getUser())
                .role(Message.Role.USER)
                .build();
        messages.addAll(historyMessages);
        messages.add(currentMessage);
        return messages;
    }

    private Message messageCacheToMessage(ChatMessageCache messageCache) {
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

    private void saveChatMessageToCache(QuestionDTO questionDTO, Message answerMessage) {
        String answerContent = answerMessage.getContent();
        AnswerCache answerCache = AnswerCache.builder()
                .answer(answerContent)
                .messageId(questionDTO.getMessageId())
                .question(questionDTO.getQuestion())
                .askTimeMills(questionDTO.getAskTimeMills())
                .answerTimeMills(System.currentTimeMillis())
                .name(answerMessage.getName())
                .answerSegment(Arrays.asList(StringUtil.foldString(answerContent, 500)))
                .currentSegment(0)
                .build();
        ChatMessageCache answerMessageCache = messageToMessageCache(answerMessage);
        ChatMessageCache questionMessageCache = ChatMessageCache.builder()
                .content(questionDTO.getQuestion())
                .name(questionDTO.getUser())
                .role(Message.Role.USER.name())
                .createTimeMills(questionDTO.getAskTimeMills())
                .build();
        answerCacheRepository.saveAnswer(answerCache);
        chatMessageRepository.add(questionDTO.getUser(), questionMessageCache);
        chatMessageRepository.add(questionDTO.getUser(), answerMessageCache);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
