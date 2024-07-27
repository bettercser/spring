package org.spring.util;

public class BeanUtil {
    public static String getDefaultSpringBeanId(Class<?> beanClazz){
        String className = beanClazz.getSimpleName();
        return Character.toLowerCase(className.charAt(0)) + className.substring(1);
    }
}
