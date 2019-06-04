package com.github.masterdxy.gateway.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SpringContext {

    private static ApplicationContext context = null;

    public static ApplicationContext initContext(Class configurationCls){
        context = new AnnotationConfigApplicationContext(configurationCls);
        return context;
    }

}
