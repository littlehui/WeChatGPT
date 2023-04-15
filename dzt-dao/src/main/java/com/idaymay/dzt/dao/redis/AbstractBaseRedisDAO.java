package com.idaymay.dzt.dao.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.RedisSerializer;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.TimeUnit;

public abstract class AbstractBaseRedisDAO<T> {

    @Autowired
    protected RedisTemplate redisTemplate;

    /**
     * 设置redisTemplate
     *
     * @param redisTemplate the redisTemplate to set
     */
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 获取 RedisSerializer
     * <br>------------------------------<br>
     */
    protected RedisSerializer getRedisSerializer() {
        return redisTemplate.getDefaultSerializer();
    }

    ValueOperations<String, T> valueOperator;

    ValueOperations<String, Collection<T>> collectionOperator;

    ListOperations<String, T> listOperator;

    SetOperations<String, T> setOperator;



    HashOperations<String, String, T> hashOperator;

    public AbstractBaseRedisDAO() {
    }

    @PostConstruct
    public void init() {
        valueOperator = redisTemplate.opsForValue();
        collectionOperator = redisTemplate.opsForValue();
        listOperator = redisTemplate.opsForList();
        setOperator = redisTemplate.opsForSet();
        hashOperator = redisTemplate.opsForHash();
    }

    protected String zone;

    public void save(String key, String hkey, T data) {
        hashOperator.put(key, hkey, data);
    }

    public Long incrementInHash(String key, String hkey, Long incrCount, Long timeOutSeconds) {
        Long count = hashOperator.increment(zone + key, hkey, incrCount);
        redisTemplate.expire(zone + key, timeOutSeconds, TimeUnit.SECONDS);
        return count;
    }

    public Map<String, T> getHash(String key) {
        return hashOperator.entries(zone + key);
    }

    public void save(String key, T data) {
        valueOperator.set(zone + key, data);
    }

    public void save(String key, T data, long timeStamp) {
        valueOperator.set(zone + key, data, timeStamp, TimeUnit.MILLISECONDS);
    }

    public void listLeftPush(String key, T data) {
        listOperator.leftPush(zone + key, data);
    }

    public void saveCollection(String key, Collection<T> datas) {
        collectionOperator.set(zone + "collection:" + key, datas);
    }

    public void saveCollection(String key, Collection<T> datas, long timeStamp) {
        collectionOperator.set(zone + "collection:" + key, datas, timeStamp, TimeUnit.MILLISECONDS);
    }

    public Collection<T> getCollection(String key) {
        return collectionOperator.get(zone + "collection:" + key);
    }

    public void listRightPush(String key, T data) {
        listOperator.rightPush(zone + "list:" + key, data);
    }

    public Long getListSize(String key) {
        return listOperator.size(zone + "list:" + key);
    }

    public T get(String key) {
        return valueOperator.get(zone + key);
    }

    public T listLeftPop(String key) {
        return listOperator.leftPop(zone + "list:" + key);
    }

    public T listRightPop(String key) {
        return listOperator.rightPop(zone + "list:" + key);
    }

    public void delete(String key) {
        redisTemplate.delete(zone + key);
    }

    public Set<String> keys(String pattern) {
        return redisTemplate.keys(pattern);
    }

    public void deleteByPattern(String pattern) {
        Set<T> keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }

    public void delete(String... keys) {
        List<String> newKeys = new ArrayList<String>();
        for (String key : keys) {
            newKeys.add(zone + key);
        }
        redisTemplate.delete(newKeys);
    }

    public void deleteCollection(String... keys) {
        List<String> newKeys = new ArrayList<String>();
        for (String key : keys) {
            newKeys.add(zone + "collection:" + key);
        }
        redisTemplate.delete(newKeys);
    }

    public Long incr(String key) {
        return redisTemplate.boundValueOps(zone + key).increment(1);
    }

    public String getZone() {
        return zone;
    }

    public List<T> lRangeAll(String key) {
        return listOperator.range(zone + key, 0, -1);
    }

    public List<T> lRangeSize(String key, Integer size) {
        return listOperator.range(zone + key, 0, size - 1);
    }

    public T leftPop(String key) {
        return listOperator.leftPop(zone + key);
    }

    public T rightPop(String key) {
        return listOperator.rightPop(zone + key);
    }

    public void leftPush(String key, T data) {
        this.listOperator.leftPush(zone + key, data);
    }

    public void rightPush(String key, T data) {
        this.listOperator.rightPush(zone + key, data);
    }

    public void setAdd(String key, T data) {
        setOperator.add(zone + key, data);
    }

    protected Long setMemberSize(String key) {
        return setOperator.size(zone + key);
    }

    public Set<T> setGet(String key) {
        return setOperator.members(zone + key);
    }

    public Long removeSet(String key, T data) {
        Long result = setOperator.remove(zone + key, data);
        return result;
    }

}
