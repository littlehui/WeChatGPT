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

    public QuestionCache getQuestionByMessageId(Long messageId) {
        return get(messageId + "");
    }

    public QuestionCache saveQuestion(String messageId, String question, String user) {
        QuestionCache questionCache = QuestionCache.builder()
                .question(question)
                .messageId(messageId)
                .user(user)
                .askTimeMills(System.currentTimeMillis())
                .build();
        this.save(messageId, questionCache);
        return questionCache;
    }
}
