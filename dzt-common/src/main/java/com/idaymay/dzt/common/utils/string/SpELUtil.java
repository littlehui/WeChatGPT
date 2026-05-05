package com.idaymay.dzt.common.utils.string;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;

/**
 * 基于连接点解析 SpEL（如限流 key）。参数名优先来自 AspectJ {@link MethodSignature}，其次
 * {@link DefaultParameterNameDiscoverer}（依赖字节码中的 {@code MethodParameters}，见根 POM {@code -parameters}）。
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
        Method method = methodSignature.getMethod();
        Object[] rawArgs = joinPoint.getArgs();
        Object[] args = rawArgs != null ? rawArgs : new Object[0];
        String[] paramNames = resolveParameterNames(methodSignature, method, args.length);
        Expression expression = parser.parseExpression(spELStr);
        EvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < args.length; i++) {
            context.setVariable(paramNames[i], args[i]);
        }
        Object value = expression.getValue(context);
        return value != null ? value.toString() : "null";
    }

    private static String[] resolveParameterNames(MethodSignature methodSignature, Method method, int argCount) {
        String[] fromAspect = methodSignature.getParameterNames();
        if (namesMatchArgCount(fromAspect, argCount)) {
            return fromAspect;
        }
        String[] discovered = nameDiscoverer.getParameterNames(method);
        if (namesMatchArgCount(discovered, argCount)) {
            return discovered;
        }
        Parameter[] parameters = method.getParameters();
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
                "无法解析方法 {}.{} 的参数名（共 {} 个形参），SpEL 将退回 arg0..；请在根 pom 的 maven-compiler-plugin 中启用 <parameters>true</parameters> 后全量重编译。",
                method.getDeclaringClass().getSimpleName(),
                method.getName(),
                argCount);
        String[] synthetic = new String[argCount];
        for (int i = 0; i < argCount; i++) {
            synthetic[i] = "arg" + i;
        }
        return synthetic;
    }

    private static boolean namesMatchArgCount(String[] names, int argCount) {
        return names != null && names.length == argCount;
    }
}
