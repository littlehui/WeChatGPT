package com.idaymay.dzt.app.web.interceptor;

import com.idaymay.dzt.bean.constant.ChatConstants;
import com.idaymay.dzt.common.constants.RateLimiterConstant;
import com.idaymay.dzt.common.exception.RateLimitException;
import com.idaymay.dzt.common.redission.NeedRateLimit;
import com.idaymay.dzt.common.utils.string.SpELUtil;
import com.idaymay.dzt.dao.redis.domain.UserConfigCache;
import com.idaymay.dzt.dao.redis.repository.FreeCountCacheRepository;
import com.idaymay.dzt.dao.redis.repository.UserConfigCacheRepository;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.redisson.Redisson;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * TODO
 *
 * @author littlehui
 * @version 1.0
 * @date 2023/04/24 10:25
 */
@Aspect
@Component
@Order(2) //该order必须设置，很关键
@Slf4j
public class RateLimiterAspect {

    @Resource
    private Redisson redisson;

    @Autowired
    FreeCountCacheRepository freeCountCacheRepository;

    @Autowired
    UserConfigCacheRepository userConfigCacheRepository;

    Map<String, RRateLimiter> rRateLimiterMap = new ConcurrentHashMap<>();

    ReentrantLock reentrantLock = new ReentrantLock();

    @Before("@annotation(needRateLimit)")
    public void before(JoinPoint joinPoint, NeedRateLimit needRateLimit) throws Throwable {
        if (joinPoint instanceof MethodInvocationProceedingJoinPoint) {
            MethodInvocationProceedingJoinPoint methodInvocationProceedingJoinPoint = (MethodInvocationProceedingJoinPoint)joinPoint;
            String limitKey =  SpELUtil.generateKeyBySpEL(needRateLimit.limitKey(), methodInvocationProceedingJoinPoint);
            String fromUser = SpELUtil.generateKeyBySpEL(needRateLimit.fromUser(), methodInvocationProceedingJoinPoint);
            String toUser =  SpELUtil.generateKeyBySpEL(needRateLimit.toUser(), methodInvocationProceedingJoinPoint);
            if (!tryAcquire(limitKey, needRateLimit.permit(), needRateLimit.timeOut())) {
                log.error("user:{}，问太快，触发限流。", fromUser);
                throw new RateLimitException(toUser, fromUser, ChatConstants.SLOWDOWN);
            } else {
                UserConfigCache userConfigCache = userConfigCacheRepository.getUserConfig(fromUser);
                if (userConfigCache != null && userConfigCache.getOpenAiApiKey() != null) {
                    //不比较
                    log.info("已经配置了apiKey不进行限制操作。");
                } else {
                    Long freeCount = freeCountCacheRepository.getUsedFreeCount(fromUser);
                    if (freeCount > ChatConstants.FREE_COUNT) {
                        log.info("超出今天的上限。");
                        throw new RateLimitException(toUser, fromUser, ChatConstants.OUT_OF_FREE_COUNT);
                    }
                }
            }
        } else {
            log.warn("不支持的切面参数类型：{}",joinPoint.getClass());
        }
    }

    public boolean tryAcquire(String key, Integer permit, Integer timeOut) {
        RRateLimiter rateLimiter = rRateLimiterMap.get(getKey(key));
        if (rateLimiter == null) {
            try {
                if (reentrantLock.tryLock(5, TimeUnit.SECONDS)) {
                    rateLimiter = rRateLimiterMap.get(getKey(key));
                    if (rateLimiter != null) {
                        reentrantLock.unlock();
                        return rateLimiter.tryAcquire();
                    } else {
                        RRateLimiter keyLimiter = redisson.getRateLimiter(getKey(key));
                        keyLimiter.trySetRate(RateType.OVERALL, permit, timeOut, RateIntervalUnit.SECONDS);
                        rateLimiter = keyLimiter;
                        rRateLimiterMap.put(getKey(key), keyLimiter);
                        reentrantLock.unlock();
                        return rateLimiter.tryAcquire();
                    }
                } else {
                    return rateLimiter.tryAcquire();
                }
            } catch (InterruptedException e) {
                if (rateLimiter != null) {
                    return rateLimiter.tryAcquire();
                } else {
                    log.error("key:{}，限流初始化异常。不进行限流。", key);
                    return true;
                }
            } finally {
                if (reentrantLock.isHeldByCurrentThread()) {
                    reentrantLock.unlock();
                }
            }
        } else {
            return rateLimiter.tryAcquire();
        }
    }

    private String getKey(String key) {
        return "RateLimiter:" + key;
    }

    @PostConstruct
    public void initRate() {
        RRateLimiter questionLimiter = redisson.getRateLimiter(RateLimiterConstant.RATE_LIMIT_QUESTION);
        RRateLimiter globalLimiter = redisson.getRateLimiter(RateLimiterConstant.RATE_LIMIT_QUESTION);

        questionLimiter.trySetRate(RateType.OVERALL, 10, 1, RateIntervalUnit.SECONDS);
        globalLimiter.trySetRate(RateType.OVERALL, 20, 1, RateIntervalUnit.SECONDS);

        rRateLimiterMap.put(RateLimiterConstant.RATE_LIMIT_GLOBAL, globalLimiter);
        rRateLimiterMap.put(RateLimiterConstant.RATE_LIMIT_QUESTION, questionLimiter);
    }

}
