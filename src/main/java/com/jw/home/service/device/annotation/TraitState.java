package com.jw.home.service.device.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collections;
import java.util.Set;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TraitState {
    String[] names();
}
