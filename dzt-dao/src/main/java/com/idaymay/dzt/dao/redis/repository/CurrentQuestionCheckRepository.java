package com.idaymay.dzt.dao.redis.repository;

import com.idaymay.dzt.dao.redis.AbstractBaseRedisDAO;
import org.springframework.stereotype.Component;

/**
 * TODO
 *
 * @author littlehui
 * @version 1.0
 * @date 2023/04/29 23:56
 */
@Component
public class CurrentQuestionCheckRepository extends AbstractBaseRedisDAO<String> {

    private static final Long ONE_DAY_MILLS = 60 * 60 * 24 * 1000L;

    public CurrentQuestionCheckRepository() {
        this.zone = "Chat:CurrentQuestionCheck:";
    }

    public void setCurrentCheck(String userCode, String question) {
        this.save(userCode, question, ONE_DAY_MILLS);
    }

    public String getAndRemove(String userCode) {
        String question = this.get(userCode);
        this.delete(userCode);
        return question;
    }
}
