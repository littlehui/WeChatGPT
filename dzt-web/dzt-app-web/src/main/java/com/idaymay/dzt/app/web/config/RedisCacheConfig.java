package com.idaymay.dzt.app.web.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idaymay.dzt.common.cache.RemoveBatchAbleCacheManager;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * cache标签使用方式如下：默认缓存1分钟。
 *
 * @Caching( evict = {
 * @CacheEvict(value = "XXX:XX", key = "#XX.bookId + '*'"),
 * @CacheEvict(value = "XXX:XX", key = "#XX.id + '*'")
 * }
 * )
 * @Cacheable(value = "GameServiceImpl:findGames", key = "#status" )
 * @Description TODO
 * @ClassName RedisCacheConfig
 * @Author littlehui
 * @Date 2021/8/8 00:16
 * @Version 1.0
 **/
@Configuration
@ConditionalOnBean(value = {RedissonAutoConfiguration.class})
public class RedisCacheConfig {

    @Value("${spring.config.activate.on-profile}")
    private String activeProfile;

    private Long globalSessionTimeout = 5L;

    //1分钟
    private Long defaultCacheTimeOut = 60L;

    private String FIVE_MINUTE_CACHE = "fiveMinuteCache";

    @Value("${cache.prefix}")
    private String CACHE_VERSION;

    //永久缓存
    private Long CACHE_FOREVER = 0L;

    private String CACHE_FOREVER_COMMODITY_LIST = "CommodityServiceImpl:findCommodities";

    //1秒钟
    private Long CACHE_ONE_SECOND = 1L;

    private String CACHE_ONE_SECOND_ORDER_DETAIL = "OrderServiceImpl:orderDetail";

    private String CACHE_ONE_SECOND_ORDER_PAYCODE = "OrderServiceImpl:orderPayCode";

    private String CACHE_ONE_SECOND_REPAIR_ORDER_DETAIL = "RepairPayOrderServiceImpl:orderDetail";

    private String CACHE_ONE_SECOND_REPAIR_ORDER_PAYCODE = "RepairPayOrderServiceImpl:orderPayCode";

    @Bean
    public RedisCacheManager redisCacheManager(RedisTemplate redisTemplate) {
        RemoveBatchAbleCacheManager removeBatchAbleCacheManager = new RemoveBatchAbleCacheManager(
                redisTemplate.getConnectionFactory()
                , getDefaultCacheConfiguration()
                , getCacheConfigurations()
                , true);
        return removeBatchAbleCacheManager;
    }


    private Map<String, RedisCacheConfiguration> getCacheConfigurations() {
        Map<String, RedisCacheConfiguration> configurationMap = new HashMap<>();
        configurationMap.put(FIVE_MINUTE_CACHE, this.getDefaultCacheConfiguration(globalSessionTimeout * 60));
        configurationMap.put(CACHE_FOREVER_COMMODITY_LIST, this.getDefaultCacheConfiguration(CACHE_FOREVER));
        configurationMap.put(CACHE_ONE_SECOND_ORDER_DETAIL, this.getDefaultCacheConfiguration(CACHE_ONE_SECOND));
        configurationMap.put(CACHE_ONE_SECOND_ORDER_PAYCODE, this.getDefaultCacheConfiguration(CACHE_ONE_SECOND));
        configurationMap.put(CACHE_ONE_SECOND_REPAIR_ORDER_DETAIL, this.getDefaultCacheConfiguration(CACHE_ONE_SECOND));
        configurationMap.put(CACHE_ONE_SECOND_REPAIR_ORDER_PAYCODE, this.getDefaultCacheConfiguration(CACHE_ONE_SECOND));
        return configurationMap;
    }

    /**
     * 获取redis的缓存配置(针对于键)
     *
     * @param seconds 键过期时间
     * @return
     */
    private RedisCacheConfiguration getDefaultCacheConfiguration(long seconds) {
        ObjectMapper om = new ObjectMapper();
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();
        redisCacheConfiguration = redisCacheConfiguration.serializeValuesWith(
                        RedisSerializationContext
                                .SerializationPair
                                .fromSerializer(createJackson2JsonRedisSerializer(om))
                ).entryTtl(Duration.ofSeconds(seconds))
                .computePrefixWith(cacheName ->
                        CACHE_VERSION + ":" + activeProfile.concat(":").concat(cacheName).concat(":"));
        return redisCacheConfiguration;
    }

    /**
     * 获取Redis缓存配置,此处获取的为默认配置
     * 如对键值序列化方式,是否缓存null值,是否使用前缀等有特殊要求
     * 可另行调用 RedisCacheConfiguration 的构造方法
     *
     * @return
     */
    private RedisCacheConfiguration getDefaultCacheConfiguration() {
        ObjectMapper om = new ObjectMapper();
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();
        redisCacheConfiguration = redisCacheConfiguration.serializeValuesWith(
                        RedisSerializationContext
                                .SerializationPair
                                .fromSerializer(createJackson2JsonRedisSerializer(om))
                ).entryTtl(Duration.ofSeconds(defaultCacheTimeOut))
                .computePrefixWith(cacheName ->
                        CACHE_VERSION + ":" + activeProfile.concat(":").concat(cacheName).concat(":"));
        return redisCacheConfiguration;
    }

    private RedisSerializer<Object> createJackson2JsonRedisSerializer(ObjectMapper objectMapper) {
        // TODO Auto-generated method stub\
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<Object>(Object.class);
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL);
        serializer.setObjectMapper(objectMapper);
        return serializer;
    }
}

