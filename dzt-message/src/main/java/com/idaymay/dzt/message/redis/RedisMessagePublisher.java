package com.idaymay.dzt.message.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * TODO
 *
 * @author littlehui
 * @version 1.0
 * @date 2023/04/10 23:40
 */
@Component
public class RedisMessagePublisher {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    public void publishMessage(String message) {
        redisTemplate.convertAndSend(MessageConstant.CHAT_TOPIC, message);
    }
}
