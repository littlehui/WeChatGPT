package com.idaymay.dzt.service.impl;

import com.idaymay.dzt.bean.dto.QuestionDTO;
import com.idaymay.dzt.service.MessagePublisher;
import com.idaymay.dzt.service.constant.MessageConstant;
import org.redisson.Redisson;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
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
public class RedisMessagePublisher implements MessagePublisher {

    @Resource
    private Redisson redisson;

    @Override
    public <T> void publishMessage(T message) {
        redisson.getTopic(MessageConstant.CHAT_TOPIC).publish(message);
    }
}
