package com.idaymay.dzt.common.lock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description TODO
 * @ClassName RedissonLock
 * @Author littlehui
 * @Date 2021/8/8 21:42
 * @Version 1.0
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomRedissonLock {

    /**
     * 要锁哪个参数
     */
    int lockIndex() default -1;

    /**
     * 锁多久后自动释放（单位：秒）
     */
    int leaseTime() default 10;
}
