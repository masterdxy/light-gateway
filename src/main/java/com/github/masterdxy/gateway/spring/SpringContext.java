package com.github.masterdxy.gateway.spring;

import com.google.common.collect.Lists;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;
import java.util.Map;

public class SpringContext {

    private static ApplicationContext context = null;

    public static ApplicationContext initContext(Class configurationCls) {
        context = new AnnotationConfigApplicationContext(configurationCls);
        return context;
    }

    public static <T> T instance(Class<T> tClass) {
        return context.getBean(tClass);
    }


    public static <T> List<T> instances(Class<T> iCls) {
        Map<String, T> maps = context.getBeansOfType(iCls);
        if (maps == null || maps.isEmpty()) {
            return Lists.newArrayList();
        }
        List<T> beanList = Lists.newArrayList();
        maps.forEach((name, plugin) -> {
            beanList.add(plugin);
        });
        return beanList;
    }

}
