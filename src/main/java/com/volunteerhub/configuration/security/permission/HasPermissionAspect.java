package com.volunteerhub.configuration.security.permission;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@RequiredArgsConstructor
public class HasPermissionAspect {

    private final PermissionEvaluatorService permissionEvaluatorService;
    private final ExpressionParser parser = new SpelExpressionParser();
    private final DefaultParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();

    @Before("@annotation(hasPermission)")
    public void checkPermission(JoinPoint joinPoint, HasPermission hasPermission) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        StandardEvaluationContext context = new StandardEvaluationContext();
        String[] parameterNames = nameDiscoverer.getParameterNames(method);
        Object[] args = joinPoint.getArgs();
        if (parameterNames != null) {
            for (int i = 0; i < parameterNames.length; i++) {
                context.setVariable(parameterNames[i], args[i]);
            }
        }

        Long eventId = evaluateLong(context, hasPermission.eventId());
        Long postId = evaluateLong(context, hasPermission.postId());

        permissionEvaluatorService.check(hasPermission.action(), eventId, postId);
    }

    private Long evaluateLong(StandardEvaluationContext context, String expression) {
        if (expression == null || expression.isBlank()) {
            return null;
        }
        return parser.parseExpression(expression).getValue(context, Long.class);
    }
}
