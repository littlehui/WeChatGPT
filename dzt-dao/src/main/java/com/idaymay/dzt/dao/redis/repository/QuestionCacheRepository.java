package com.idaymay.dzt.dao.redis.repository;

import com.idaymay.dzt.dao.redis.AbstractBaseRedisDAO;
import com.idaymay.dzt.dao.redis.domain.QuestionCache;
import org.springframework.stereotype.Repository;

/**
 * TODO
 *
 * @author littlehui
 * @version 1.0
 * @date 2023/04/11 21:17
 */
@Repository
public class QuestionCacheRepository extends AbstractBaseRedisDAO<QuestionCache> {

    public QuestionCacheRepository() {
        this.zone = "Chat:Question:";
    }

    private final static Long TIMEOUT = 60 * 60 * 24 * 1000L;

    public QuestionCache getQuestionByMessageId(String messageId) {
        return get(messageId);
    }

    public QuestionCache saveQuestion(String messageId, String question, String user, Integer requestTimes) {
        QuestionCache questionCache = QuestionCache.builder()
                .question(question)
                .messageId(messageId)
                .userCode(user)
                .askTimeMills(System.currentTimeMillis())
                .requestEdTimes(requestTimes)
                .build();
        this.save(messageId, questionCache, TIMEOUT);
        return questionCache;
    }
}
