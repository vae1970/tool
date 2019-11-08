package com.vae1970.tool.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author dongzhou.gu
 * @date 2019/10/24
 */
@Slf4j
@Component
public class SpringContextUtil implements ApplicationContextAware, DisposableBean {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        log.info("ApplicationContextAware");
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

    @Override
    public void destroy() throws Exception {
        context = null;
    }
}
