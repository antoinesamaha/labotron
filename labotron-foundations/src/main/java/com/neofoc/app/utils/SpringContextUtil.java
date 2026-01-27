package com.neofoc.app.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Utility class to access Spring beans from non-Spring managed components.
 * This is particularly useful for:
 * - Static utility methods
 * - Legacy code integration
 * - Non-Spring managed threads
 */
@Component
public class SpringContextUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtil.applicationContext = applicationContext;
    }

    /**
     * Get a Spring bean by class.
     *
     * @param clazz the bean class
     * @param <T>   the bean type
     * @return the bean instance
     */
    public static <T> T getBean(Class<T> clazz) {
        if (applicationContext == null) {
            throw new IllegalStateException("ApplicationContext has not been set. " +
                    "Ensure SpringContextUtil is configured as a Spring bean.");
        }
        return applicationContext.getBean(clazz);
    }

    /**
     * Get a Spring bean by name and class.
     *
     * @param name  the bean name
     * @param clazz the bean class
     * @param <T>   the bean type
     * @return the bean instance
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        if (applicationContext == null) {
            throw new IllegalStateException("ApplicationContext has not been set. " +
                    "Ensure SpringContextUtil is configured as a Spring bean.");
        }
        return applicationContext.getBean(name, clazz);
    }

    /**
     * Get a Spring bean by name.
     *
     * @param name the bean name
     * @return the bean instance
     */
    public static Object getBean(String name) {
        if (applicationContext == null) {
            throw new IllegalStateException("ApplicationContext has not been set. " +
                    "Ensure SpringContextUtil is configured as a Spring bean.");
        }
        return applicationContext.getBean(name);
    }

    /**
     * Get the Spring application context.
     *
     * @return the application context
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
