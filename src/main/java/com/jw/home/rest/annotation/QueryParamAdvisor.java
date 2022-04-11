package com.jw.home.rest.annotation;

import com.jw.home.exception.BadRequestParamException;
import com.jw.home.utils.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.lang.reflect.Method;

@Aspect
@Slf4j
@Component
public class QueryParamAdvisor {
    @Around("@annotation(com.jw.home.rest.annotation.QueryParam)")
    public Object processCustomAnnotation(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object[] args = proceedingJoinPoint.getArgs();
        MultiValueMap<String, String> queryParams = ((ServerRequest) args[0]).queryParams();

        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        QueryParam queryParamAnnotation = methodSignature.getMethod().getAnnotation(QueryParam.class);

        Method validateMethod = queryParamAnnotation.validator().getMethod("validate", MultiValueMap.class);
        boolean isValid = (boolean) validateMethod.
                invoke(BeanUtil.getBean(queryParamAnnotation.validator()), queryParams);
        if (!isValid) {
            throw BadRequestParamException.INSTANCE;
        }
        return proceedingJoinPoint.proceed();
    }
}
