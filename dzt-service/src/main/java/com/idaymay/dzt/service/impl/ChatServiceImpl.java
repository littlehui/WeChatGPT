package com.idaymay.dzt.service.impl;

import com.idaymay.dzt.bean.dto.QuestionDTO;
import com.idaymay.dzt.common.utils.string.StringUtil;
import com.idaymay.dzt.dao.redis.domain.AnswerCache;
import com.idaymay.dzt.dao.redis.domain.QuestionCache;
import com.idaymay.dzt.dao.redis.repository.AnswerCacheRepository;
import com.idaymay.dzt.dao.redis.repository.QuestionCacheRepository;
import com.idaymay.dzt.service.ChatService;
import com.idaymay.dzt.service.MessagePublisher;
import com.idaymay.dzt.service.constant.ChatConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * TODO
 *
 * @author littlehui
 * @version 1.0
 * @date 2023/04/11 21:26
 */
@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    AnswerCacheRepository answerCacheRepository;

    @Autowired
    QuestionCacheRepository questionCacheRepository;

    @Autowired
    SnowflakeIdGenerator snowflakeIdGenerator;

    @Autowired
    MessagePublisher messagePublisher;

    @Resource
    Jackson2JsonRedisSerializer<QuestionDTO> jackson2JsonRedisSerializer;

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
        if (answerCache != null) {
            return answerCache.getAnswer();
        }
        return ChatConstants.DEFAULT_ANSWER;
    }
}
