package com.idaymay.dzt.dao.redis.repository;

import com.idaymay.dzt.dao.redis.AbstractBaseRedisDAO;
import org.springframework.stereotype.Repository;

/**
 * 记录当前正在回答的messageId
 * @author littlehui
 * @version 1.0
 * @date 2023/04/26 16:50
 */
@Repository
public class CurrentAnswerQuestionRepository extends AbstractBaseRedisDAO<String> {

    private static final Long ONE_DAY_MILLS = 30 * 60 * 1000L;

    public CurrentAnswerQuestionRepository() {
        this.zone = "Chat:CurrentAnswerQuestion:";
    }

    public String getMessageId(String userCode) {
        return get(userCode);
    }

    public void setCurrentAnswerMessageId(String userCode, String messageId) {
        this.save(userCode, messageId, ONE_DAY_MILLS);
    }
}

