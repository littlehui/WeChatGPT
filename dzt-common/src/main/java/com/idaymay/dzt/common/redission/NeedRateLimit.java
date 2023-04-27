package com.idaymay.dzt.common.redission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * TODO
 *
 * @author littlehui
 * @version 1.0
 * @date 2023/04/24 10:24
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NeedRateLimit {

    String limitKey() default "GLOBAL";

    String fromUser() default "";

    String toUser() default "";

    int permit() default 10;

    int timeOut() default 1;

}
