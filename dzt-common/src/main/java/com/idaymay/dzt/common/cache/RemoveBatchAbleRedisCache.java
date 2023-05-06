package com.idaymay.dzt.common.cache;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;

/**
 * @Description TODO
 * @ClassName RemoveBatchAbleRedisCache
 * @Author littlehui
 * @Date 2021/8/9 11:13
 * @Version 1.0
 **/
public class RemoveBatchAbleRedisCache extends RedisCache {

    private final String name;

    private final RedisCacheWriter cacheWriter;

    private final ConversionService conversionService;

    protected RemoveBatchAbleRedisCache(String name, RedisCacheWriter cacheWriter, RedisCacheConfiguration cacheConfig) {
        super(name, cacheWriter, cacheConfig);
        this.name = name;
        this.cacheWriter = cacheWriter;
        this.conversionService = cacheConfig.getConversionService();
    }

    @Override
    public void evict(Object key) {
        if (key instanceof String) {
            String keyString = key.toString();
            if (StringUtils.endsWith(keyString, "*")) {
                evictLikeSuffix(keyString);
                return;
            }
            if (StringUtils.startsWith(keyString, "*")) {
                evictLikePrefix(keyString);
                return;
            }
        }
        super.evict(key);
    }

    /**
     * 前缀匹配
     *
     * @param key
     */
    public void evictLikePrefix(String key) {
        byte[] pattern = this.conversionService.convert(this.createCacheKey(key), byte[].class);
        this.cacheWriter.clean(this.name, pattern);
    }

    /**
     * 后缀匹配
     *
     * @param key
     */
    public void evictLikeSuffix(String key) {
        byte[] pattern = (byte[]) this.conversionService.convert(this.createCacheKey(key), byte[].class);
        this.cacheWriter.clean(this.name, pattern);
    }
}
