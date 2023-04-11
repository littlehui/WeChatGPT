package com.idaymay.dzt.dao.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.RedisSerializer;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by mac on 15/4/22.
 */

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


    ValueOperations<String, T> valueOper;
    ValueOperations<String, Collection<T>> collectionOper;
    ListOperations<String, T> listOper;
    SetOperations<String, T> setOperater;
    HashOperations<String, String, T> hashOperater;
    public AbstractBaseRedisDAO() {
    }

    @PostConstruct
    public void init() {
        valueOper = redisTemplate.opsForValue();
        collectionOper = redisTemplate.opsForValue();
        listOper = redisTemplate.opsForList();
        setOperater = redisTemplate.opsForSet();
        hashOperater = redisTemplate.opsForHash();
    }

    protected String zone;

    public void save(String key, String hkey, T data) {
        hashOperater.put(key, hkey, data);
    }

    public Long incrementInHash(String key, String hkey, Long incrCount, Long timeOutSeconds) {
        Long count = hashOperater.increment(zone + key, hkey, incrCount);
        redisTemplate.expire(zone + key, timeOutSeconds, TimeUnit.SECONDS);
        return count;
    }

    public Map<String, T> getHash(String key) {
        return hashOperater.entries(zone + key);
    }

    public void save(String key, T data) {
        valueOper.set(zone + key, data);
    }

    public void save(String key, T data, long timeStamp) {
        valueOper.set(zone + key, data, timeStamp, TimeUnit.MILLISECONDS);
    }

    public void listLeftPush(String key, T data) {
        listOper.leftPush(zone + key, data);
    }

    public void saveCollection(String key, Collection<T> datas) {
        collectionOper.set(zone + "collection:" + key, datas);
    }

    public void saveCollection(String key, Collection<T> datas, long timeStamp) {
        collectionOper.set(zone + "collection:" + key, datas, timeStamp, TimeUnit.MILLISECONDS);
    }

    public Collection<T> getCollection(String key) {
        return collectionOper.get(zone + "collection:" + key);
    }

    public void listRightPush(String key, T data) {
        listOper.rightPush(zone + "list:" + key, data);
    }

    public Long getListSize(String key) {
        return listOper.size(zone + "list:" + key);
    }

    public T get(String key) {
        return valueOper.get(zone + key);
    }

    public T listLeftPop(String key) {
        return listOper.leftPop(zone + "list:" + key);
    }

    public T listRightPop(String key) {
        return listOper.rightPop(zone + "list:" + key);
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
        return listOper.range(zone + key, 0, -1);
    }

    public List<T> lRangeSize(String key, Integer size) {
        return listOper.range(zone + key, 0, size - 1);
    }

    public T leftPop(String key) {
        return listOper.leftPop(zone + key);
    }

    public T rightPop(String key) {
        return listOper.rightPop(zone + key);
    }

    public void leftPush(String key, T data) {
        this.listOper.leftPush(zone + key, data);
    }

    public void rightPush(String key, T data) {
        this.listOper.rightPush(zone + key, data);
    }
    public void setAdd(String key, T data) {
        setOperater.add(zone + key, data);
    }

    protected Long setMemberSize(String key) {
        return setOperater.size(zone + key);
    }


    public Set<T> setGet(String key) {
        return setOperater.members(zone + key);
    }

    public Long removeSet(String key, T data) {
        Long result = setOperater.remove(zone + key, data);
        return result;
    }

}
