package com.idaymay.dzt.app.web.service;

import com.idaymay.dzt.app.web.interceptor.RateLimiterAspect;
import com.idaymay.dzt.app.web.service.common.BaseTestService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.TimeUnit;

/**
 * TODO
 *
 * @author littlehui
 * @version 1.0
 * @date 2023/04/25 17:51
 */
@ActiveProfiles("local")
@Slf4j
public class RateLimiterTests extends BaseTestService {

    @Autowired
    RateLimiterAspect aspect;

    @Test
    public void tryAcquire() throws InterruptedException {
        log.info("获取锁：{}", aspect.tryAcquire("test", 1, 10));
        for (int i = 0; i<10; i++) {
            Thread.sleep(1000);
            log.info("获取锁：{}", aspect.tryAcquire("test", 1, 10));
        }
    }
}
