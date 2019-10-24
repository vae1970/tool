package com.vae1970.tool.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author dongzhou.gu
 * @date 2019/10/24
 */
public class SpringContextUtil implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    @SuppressWarnings("UnusedReturnValue")
    public static ApplicationContext getApplicationContext() {
        return context;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String beanName) throws BeansException {
        return (T) context.getBean(beanName);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(Class classType) throws BeansException {
        return (T) context.getBean(classType);
    }

}
