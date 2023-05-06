package com.idaymay.dzt.dao.redis.repository;

import com.idaymay.dzt.bean.constant.ChatConstants;
import com.idaymay.dzt.dao.redis.AbstractBaseRedisDAO;
import com.idaymay.dzt.dao.redis.domain.AnswerCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

/**
 * TODO
 *
 * @author littlehui
 * @version 1.0
 * @date 2023/04/11 21:10
 */
@Repository
@Slf4j
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
                .answer(ChatConstants.THINKING)
                .askTimeMills(System.currentTimeMillis())
                .name(user)
                .question(question)
                .messageId(messageId)
                .build();
        saveAnswer(answerCache);
        return answerCache;
    }

    public void resetAnswerCurrentSegment(String messageId) {
        synchronized (messageId) {
            log.info("重置Segment,如果已经回答的话。");
            AnswerCache answerCache = get(messageId);
            if (answerCache != null) {
                answerCache.setCurrentSegment(0);
                saveAnswer(answerCache);
            }
        }
    }

    public void incrAnswerCurrentSegment(String messageId) {
        synchronized (messageId) {
            AnswerCache answerCache = getAnswerByMessageId(messageId);
            log.info("segment + 1");
            Integer currentSegment = answerCache.getCurrentSegment();
            Integer segmentCount = answerCache.getAnswerSegment().size();
            Integer nextSegment = currentSegment + 1;
            log.info("已读一页，Segment + 1 下个Segment:{}", nextSegment);
            if (nextSegment >= segmentCount) {
                nextSegment = 0;
            }
            answerCache.setCurrentSegment(nextSegment);
            saveAnswer(answerCache);
        }
    }
}
