package com.jw.home.utils;

import com.jw.home.ApplicationContextProvider;
import org.springframework.context.ApplicationContext;

public class BeanUtil {
    public static Object getBean(Class<?> clazz) {
        ApplicationContext applicationContext = ApplicationContextProvider.getApplicationContext();
        return applicationContext.getBean(clazz);
    }
}
