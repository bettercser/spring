package org.spring.def;

public class BeanDefination {

    private String beanName;

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    private Class<?> aClass;

    private String Scoped;

    public Class<?> getaClass() {
        return aClass;
    }

    public void setaClass(Class<?> aClass) {
        this.aClass = aClass;
    }

    public String getScoped() {
        return Scoped;
    }

    public void setScoped(String scoped) {
        Scoped = scoped;
    }
}
