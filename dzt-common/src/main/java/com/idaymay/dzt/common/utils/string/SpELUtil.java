package com.idaymay.dzt.common.utils.string;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;

/**
 * TODO
 *
 * @author littlehui
 * @version 1.0
 * @date 2023/04/25 09:39
 */
public class SpELUtil {

    private static final SpelExpressionParser parser = new SpelExpressionParser();

    private static final DefaultParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();

    public static String generateKeyBySpEL(String spELStr, ProceedingJoinPoint proceedingJoinPoint) {
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = methodSignature.getMethod();
        String[] paramsNames = nameDiscoverer.getParameterNames(method);
        Expression expression = parser.parseExpression(spELStr);
        EvaluationContext context = new StandardEvaluationContext();
        Object[] args = proceedingJoinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            context.setVariable(paramsNames[i], args[i]);
        }
        return expression.getValue(context).toString();
    }
}
