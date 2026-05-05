package com.idaymay.dzt.common.utils.string;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * 基于连接点解析 SpEL（如限流 key）。<br>
 * JDK 动态代理时 {@link MethodSignature#getMethod()} 常为<strong>接口</strong>上的 {@link Method}，
 * 若接口 class 未带参数名元数据而实现类有，则 {@link DefaultParameterNameDiscoverer} 会得到 {@code null}。<br>
 * 此处先解析到<strong>目标实现类</strong>上的具体方法再取参数名；仍依赖根 POM 的 {@code -parameters} 以写入字节码。
 */
public class SpELUtil {

    private static final Logger log = LoggerFactory.getLogger(SpELUtil.class);

    private static final SpelExpressionParser parser = new SpelExpressionParser();

    private static final DefaultParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();

    public static String generateKeyBySpEL(String spELStr, JoinPoint joinPoint) {
        if (!StringUtils.hasText(spELStr)) {
            throw new IllegalArgumentException("spELStr must not be empty");
        }
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method signatureMethod = methodSignature.getMethod();
        Object[] rawArgs = joinPoint.getArgs();
        Object[] args = rawArgs != null ? rawArgs : new Object[0];
        Method methodForNames = resolveMethodForParameterNames(joinPoint, signatureMethod);
        String[] paramNames = resolveParameterNames(methodSignature, methodForNames, signatureMethod, args.length);
        Expression expression = parser.parseExpression(spELStr);
        EvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < args.length; i++) {
            context.setVariable(paramNames[i], args[i]);
        }
        Object value = expression.getValue(context);
        return value != null ? value.toString() : "null";
    }

    /**
     * 解析到带真实字节码的实现类方法（避开 JDK 代理上的接口 Method）。
     */
    private static Method resolveMethodForParameterNames(JoinPoint joinPoint, Method signatureMethod) {
        Object target = joinPoint.getTarget();
        if (target == null) {
            return signatureMethod;
        }
        Class<?> targetClass = AopUtils.getTargetClass(target);
        Method specific = ClassUtils.getMostSpecificMethod(signatureMethod, targetClass);
        return specific != null ? specific : signatureMethod;
    }

    private static String[] resolveParameterNames(
            MethodSignature methodSignature, Method methodForNames, Method signatureMethod, int argCount) {
        String[] discovered = nameDiscoverer.getParameterNames(methodForNames);
        if (namesUsable(discovered, argCount)) {
            return discovered;
        }
        String[] fromAspect = methodSignature.getParameterNames();
        if (namesUsable(fromAspect, argCount)) {
            return fromAspect;
        }
        if (signatureMethod != methodForNames) {
            discovered = nameDiscoverer.getParameterNames(signatureMethod);
            if (namesUsable(discovered, argCount)) {
                return discovered;
            }
        }
        Parameter[] parameters = methodForNames.getParameters();
        if (parameters.length == argCount) {
            String[] fromReflect = new String[argCount];
            boolean allNamed = true;
            for (int i = 0; i < argCount; i++) {
                if (!parameters[i].isNamePresent()) {
                    allNamed = false;
                    break;
                }
                fromReflect[i] = parameters[i].getName();
            }
            if (allNamed) {
                return fromReflect;
            }
        }
        log.warn(
                "无法解析方法 {}.{}（实现类 {}）的参数名（共 {} 个形参），SpEL 将退回 arg0..；请确认根 pom 已启用 <parameters>true</parameters> 且对 dzt-service 执行 clean 后全量编译。",
                signatureMethod.getDeclaringClass().getSimpleName(),
                signatureMethod.getName(),
                methodForNames.getDeclaringClass().getSimpleName(),
                argCount);
        String[] synthetic = new String[argCount];
        for (int i = 0; i < argCount; i++) {
            synthetic[i] = "arg" + i;
        }
        return synthetic;
    }

    /** 与实参个数一致且每个名字非空（避免误用空串占位）。 */
    private static boolean namesUsable(String[] names, int argCount) {
        if (names == null || names.length != argCount) {
            return false;
        }
        for (String n : names) {
            if (!StringUtils.hasText(n)) {
                return false;
            }
        }
        return true;
    }
}
