package com.idaymay.dzt.dao.redis.optype;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.Set;

/**
 * TODO
 *
 * @author littlehui
 * @version 1.0
 * @date 2023/04/16 00:48
 */
public class BaseRedisZSet<T extends SortScore> {

    @Autowired
    RedisTemplate redisTemplate;

    private ZSetOperations<String, T> operator;

    public String zone;

    private static final Integer MAX_SIZE = 10;

    public BaseRedisZSet() {
        this.zone = "SortSet:";
    }

    @PostConstruct
    public void init() {
        operator = redisTemplate.opsForZSet();
    }

    public Boolean add(String key, T t) {
        Boolean addResult = operator.add(getKey(key), t, t.getScore());
        removeIfNeed(key);
        return addResult;
    }

    private void removeIfNeed(String key) {
        if (operator.zCard(getKey(key)) > MAX_SIZE) {
            operator.removeRange(getKey(key), 0, (operator.zCard(getKey(key)) - MAX_SIZE) - 1);
        }
    }

    public Long remove(String key, T t) {
        return operator.remove(getKey(key), t);
    }

    public T randMember(String key) {
        return operator.randomMember(getKey(key));
    }

    public Set<T> reverseRange(String key, Long topNum) {
        Long totalNum = operator.size(getKey(key));
        if (totalNum.intValue() == 0) {
            return Collections.emptySet();
        }
        topNum = totalNum >= topNum ? topNum : totalNum;
        return operator.reverseRange(getKey(key), 0, topNum - 1);
    }

    private String getKey(String key) {
        return zone + key;
    }
}
