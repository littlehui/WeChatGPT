package com.idaymay.dzt.app.web.service;

import com.idaymay.dzt.app.web.service.common.BaseTestService;
import com.idaymay.dzt.service.impl.RedisMessagePublisher;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;


/**
 * TODO
 *
 * @author littlehui
 * @version 1.0
 * @date 2023/04/10 23:57
 */
@ActiveProfiles("local")
public class RedisMessagePublisherTests extends BaseTestService {

    @Resource
    RedisMessagePublisher redisMessagePublisher;

    @Test
    public void publishMessage() throws InterruptedException {
        redisMessagePublisher.publishMessage("this is a test message !");
        Thread.sleep(50000);
    }
}
