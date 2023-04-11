package com.idaymay.dzt.dao.redis.repository;

import com.idaymay.dzt.dao.redis.AbstractBaseRedisDAO;
import com.idaymay.dzt.dao.redis.domain.AnswerCache;
import org.springframework.stereotype.Repository;

/**
 * TODO
 *
 * @author littlehui
 * @version 1.0
 * @date 2023/04/11 21:10
 */
@Repository
public class AnswerCacheRepository extends AbstractBaseRedisDAO<AnswerCache> {

    private final static Long TIMEOUT = 60 * 60 * 24 * 1000L;

    public AnswerCacheRepository() {
        this.zone = "Chat:Answer:";
    }

    public AnswerCache getAnswerByMessageId(String messageId) {
        return get(messageId);
    }

    public void saveAnswer(AnswerCache answerCache) {
        this.save(answerCache.getMessageId(), answerCache, TIMEOUT);
    }

    public AnswerCache initAnswer(String messageId, String question, String user) {
        AnswerCache answerCache = AnswerCache.builder()
                .answer("解答中，请稍后！")
                .askTimeMills(System.currentTimeMillis())
                .name(user)
                .question(question)
                .messageId(messageId)
                .build();
        saveAnswer(answerCache);
        return answerCache;
    }
}
