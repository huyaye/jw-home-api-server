package com.jw.home.rest.annotation;

import com.jw.home.rest.validator.RequestParamValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface QueryParam {
    Class<? extends RequestParamValidator> validator();
}
