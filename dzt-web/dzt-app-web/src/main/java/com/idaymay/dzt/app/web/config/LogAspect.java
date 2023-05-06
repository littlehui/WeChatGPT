package com.idaymay.dzt.app.web.config;

import com.idaymay.dzt.app.web.utils.WebUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * <p>
 * 使用AOP记录访问日志
 * 使用@Before在切入点开始处切入内容
 * 使用@After在切入点结尾处切入内容
 * 使用@AfterReturning在切入点return内容之后切入内容（可以用来对处理返回值做一些加工处理）
 * 使用@Around在切入点前后切入内容，并自己控制何时执行切入点自身的内容
 * 使用@AfterThrowing用来处理当切入内容部分抛出异常之后的处理逻辑
 * <p>
 * 注解：
 * Aspect:AOP
 * Component：Bean
 * Slf4j：可以直接使用log输出日志
 * Order：多个AOP切同一个方法时的优先级，越小优先级越高越大。
 * 在切入点前的操作，按order的值由小到大执行
 * 在切入点后的操作，按order的值由大到小执行
 *
 * @Author niujinpeng
 * @Date 2019/1/4 23:29
 */

@Aspect
@Component
@Slf4j
@Order(1)
public class LogAspect {
    /**
     * 线程存放信息
     */
    ThreadLocal<Long> startTime = new ThreadLocal<>();

    /**
     * 定义切入点
     * 第一个*：标识所有返回类型
     * 字母路径：包路径
     * 两个点..：当前包以及子包
     * 第二个*：所有的类
     * 第三个*：所有的方法
     * 最后的两个点：所有类型的参数
     */
    @Pointcut("execution(public * com.idaymay.dzt.app.web.controller..*.*(..))")
    public void webLog() {
    }

    /**
     * 在切入点开始处切入内容
     *
     * @param joinPoint
     */
    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint) {
        // 记录请求时间
        startTime.set(System.currentTimeMillis());
        // 获取请求域
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        // 记录请求内容
        StringBuffer logBuffer = new StringBuffer();
        logBuffer.append("Aspect-URL: ").append(request.getRequestURI().toLowerCase()).append("\n");
        logBuffer.append("Aspect-HTTP_METHOD: ").append(request.getMethod()).append("\n");
        logBuffer.append("Aspect-IP: ").append(WebUtil.getIpAddress(request)).append("\n");
        logBuffer.append("Aspect-REQUEST_METHOD: ").append(joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName()).append("\n");
        logBuffer.append("Aspect-Args: ").append(Arrays.toString(joinPoint.getArgs()));
        logBuffer.append("Aspect-QueryString: ").append(request.getQueryString());
        log.info(logBuffer.toString());
    }

    /**
     * 在切入点之后处理内容
     */
    @After("webLog()")
    public void doAfter() {

    }

    /**
     * 在切入点return内容之后切入内容（可以用来对处理返回值做一些加工处理）
     */
    @AfterReturning(returning = "ret", pointcut = "webLog()")
    public void doAfterReturning(Object ret) throws Throwable {
        String response = "";
        if (ret != null) {
            response = ret.toString();
        }
        StringBuffer logBuffer = new StringBuffer();
        logBuffer.append("Aspect-Response: ").append(response);
        Long endTime = System.currentTimeMillis();
        Long costTimeMills = endTime - startTime.get();
        logBuffer.append("\nAspect-SpeedTime: ").append(costTimeMills).append("ms");
        if (costTimeMills > 5000) {
            log.warn("执行时间大于5秒。{}", logBuffer.toString());
        }
        log.info(logBuffer.toString());
    }
}
