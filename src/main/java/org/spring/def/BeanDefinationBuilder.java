package org.spring.def;

import jdk.nashorn.internal.runtime.Scope;
import org.spring.Component;
import org.spring.Scoped;
import org.w3c.dom.Element;

public class BeanDefinationBuilder {


    public static BeanDefination genericBeanDefinationByClassConfig(Class<?> beanClazz){
        BeanDefination beanDefination = new BeanDefination();
        if(beanClazz.isAnnotationPresent(Component.class)){
            beanDefination.setaClass(beanClazz);
            beanDefination.setBeanName(beanClazz.getName());
            if(beanClazz.isAnnotationPresent(Scoped.class)){
                Scoped beanScoped = beanClazz.getDeclaredAnnotation(Scoped.class);
                String scoped = beanScoped.value();
                beanDefination.setScoped(scoped);
            }else {
                String scoped = "singleton";

                beanDefination.setScoped(scoped);
            }
        }
        return beanDefination;

    }

    public static BeanDefination genericBeanDefinationByXmlConfig(Element beanEle, ClassLoader classLoader) throws ClassNotFoundException {
        BeanDefination beanDefination = new BeanDefination();
        String className = beanEle.getAttribute("class");
        Class<?> clazz = classLoader.loadClass(className);
        String scoped = beanEle.getAttribute("scope");
        beanDefination.setBeanName(className);
        beanDefination.setScoped(scoped);
        beanDefination.setaClass(clazz);
        return beanDefination;
    }
}
