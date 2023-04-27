package com.idaymay.dzt.dao.redis.repository;

import cn.hutool.core.date.DateUtil;
import com.idaymay.dzt.dao.redis.AbstractBaseRedisDAO;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

/**
 * TODO
 *
 * @author littlehui
 * @version 1.0
 * @date 2023/04/26 16:50
 */
@Repository
public class FreeCountCacheRepository extends AbstractBaseRedisDAO<Long> {

    private static final Long ONE_DAY_MILLS = 60 * 60 * 24 * 1000L;

    public FreeCountCacheRepository() {
        this.zone = "Chat:FreeCount:";
    }

    public Long getUsedFreeCount(String userCode) {
        Integer count = (Integer) redisTemplate.boundValueOps(getKey(userCode)).get();
        return count == null ? 0 : Long.parseLong(count + "");
    }

    public Long incrUsedFreeCount(String userCode) {
        Long count = incr(getKey(userCode));
        redisTemplate.expire(getKey(userCode), ONE_DAY_MILLS, TimeUnit.MILLISECONDS);
        return count;
    }

    public String getKey(String userCode) {
        String key = DateUtil.today() + ":" + userCode;
        return key;
    }
}

