package com.idaymay.dzt.app.web.interceptor;

import com.idaymay.dzt.common.redission.CustomRedissonLock;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @Description TODO
 * @ClassName RedissonLockAspect
 * @Author littlehui
 * @Date 2021/8/8 21:44
 * @Version 1.0
 **/
@Aspect
@Component
@Order(1) //该order必须设置，很关键
@Slf4j
@ConditionalOnBean(value = {RedissonAutoConfiguration.class})
public class RedissonLockAspect {

    @Resource
    private Redisson redisson;

    @Around("@annotation(customRedissonLock)")
    public Object around(ProceedingJoinPoint joinPoint, CustomRedissonLock customRedissonLock) throws Throwable {
        Object obj = null;
        //方法内的所有参数
        Object[] params = joinPoint.getArgs();
        int lockIndex = customRedissonLock.lockIndex();
        //取得方法名
        String key = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint
                .getSignature().getName();
        //-1代表锁整个方法，而非具体锁哪条数据
        if (lockIndex != -1) {
            key += params[lockIndex];
        }
        //多久会自动释放，默认5秒
        int leaseTime = customRedissonLock.leaseTime();
        int waitTime = 30;
        RLock rLock = redisson.getLock(key);
        boolean res = rLock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);
        if (res) {
            log.info(joinPoint.getTarget().getClass() + ",取到锁:" + key);
            obj = joinPoint.proceed();
            if (rLock.isHeldByCurrentThread()) {
                rLock.unlock();
                log.info(joinPoint.getTarget().getClass() + ",释放锁:" + key);
            } else {
                log.info(joinPoint.getTarget().getClass() + ",超时已自动释放:" + key);
            }
            log.info(joinPoint.getTarget().getClass() + ",释放锁:" + key);
        } else {
            log.info("----------nono----------");
            throw new RuntimeException("没有获得锁");
        }
        return obj;
    }
}
