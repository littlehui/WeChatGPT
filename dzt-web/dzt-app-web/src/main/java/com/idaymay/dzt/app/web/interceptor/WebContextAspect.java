package com.idaymay.dzt.app.web.interceptor;

import com.idaymay.dzt.bean.constant.ChatConstants;
import com.idaymay.dzt.bean.wechat.WeChatMessage;
import com.idaymay.dzt.common.exception.RateLimitException;
import com.idaymay.dzt.common.redission.NeedRateLimit;
import com.idaymay.dzt.common.servlet.WebContext;
import com.idaymay.dzt.common.utils.string.SpELUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * TODO
 *
 * @author littlehui
 * @version 1.0
 * @date 2023/04/25 18:41
 */
@Aspect
@Component
@Order(3) //该order必须设置，很关键
@Slf4j
public class WebContextAspect {

    @Pointcut("execution(public * com.idaymay.dzt.app.web.controller..*.*(..))")
    public void webContext() {
    }

    @Before("webContext()")
    public void before(JoinPoint joinPoint) throws Throwable {
        if (joinPoint instanceof MethodInvocationProceedingJoinPoint) {
            MethodInvocationProceedingJoinPoint methodInvocationProceedingJoinPoint = (MethodInvocationProceedingJoinPoint)joinPoint;
            Object[] args = methodInvocationProceedingJoinPoint.getArgs();
            if (args != null && args.length > 0) {
                Object firstArg = args[0];
                if (firstArg instanceof WeChatMessage) {
                    WeChatMessage weChatMessage = (WeChatMessage) firstArg;
                    WebContext.setUserCode(weChatMessage.getFromUserName());
                }
            }
        } else {
            log.warn("不支持的切面参数类型：{}",joinPoint.getClass());
        }
    }
}
