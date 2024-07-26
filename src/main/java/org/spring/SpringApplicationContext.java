package org.spring;

import javafx.beans.binding.ObjectExpression;
import org.spring.def.BeanDefination;

import javax.print.DocFlavor;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SpringApplicationContext {
    // setting class
    private Class configClass ;
    //
    private final ConcurrentHashMap<String, Object> singletonHashMap = new ConcurrentHashMap<>();

    //获取 每个bean的定义情况 根据该表来设置 单例池
    private final ConcurrentHashMap<String, BeanDefination> beanDefinationHashMap = new ConcurrentHashMap<>();
    public SpringApplicationContext(Class configClass) throws IOException{
        scan(configClass);

        for(Map.Entry<String, BeanDefination> entry : beanDefinationHashMap.entrySet()){

            String beanName = entry.getKey();
            BeanDefination beanDefination = entry.getValue();
            if("singleton".equals(beanDefination.getScoped())){
                singletonHashMap.put(beanName, creatBean(beanName));
            }
        }
    }

    private void scan(Class<?> configClass) throws IOException {
        ComponentScan configClassComponentScan = configClass.getDeclaredAnnotation(ComponentScan.class);
        if(configClassComponentScan == null){
            return;
        }
        ClassLoader classLoader = SpringApplicationContext.class.getClassLoader();
        String packageName = configClassComponentScan.value();

        String packagePath = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(packagePath);
        List<String> allClassName = getAllClassName(resources);

        for(String className: allClassName){
            try {
                Class<?> clazz = classLoader.loadClass(className);
                BeanDefination beanDefination = new BeanDefination();
                if(clazz.isAnnotationPresent(Component.class)){
                    if(clazz.isAnnotationPresent(Scoped.class)){
                        Scoped clazzScoped = clazz.getDeclaredAnnotation(Scoped.class);
                        String scoped = clazzScoped.value();
                        beanDefination.setScoped(scoped);
                    }else {
                        String scoped = "singleton";
                        beanDefination.setScoped(scoped);
                    }
                    beanDefination.setaClass(clazz);
                    beanDefinationHashMap.put(className, beanDefination);
                }

            }catch (ClassNotFoundException e){

            }

        }
    }


    private List<String> getAllClassName(Enumeration<URL> resources){
        List<File> dir = new ArrayList<>();

        while (resources.hasMoreElements()){
            URL resource = resources.nextElement();
            File file = new File(resource.getFile());
            dir.add(file);
        }

        List<String> allClassName = new ArrayList<>();
        for(File e : dir){
            allClassName.addAll(getClassInDirectory(e));
        }

        return allClassName;
    }
    private List<String> getClassInDirectory(File directory){
        List<String> allClassName = new ArrayList<>();
        if(!directory.exists()){
            return allClassName;
        }
        File[] files = directory.listFiles();
        for(File file : files){
            if(file.isFile()) {
                String classPath = file.getAbsolutePath();
                String className = classPath.substring(classPath.indexOf("org"), classPath.indexOf(".class"))
                        .replace('\\','.');

                allClassName.add(className);
            }else if(file.isDirectory()){
                allClassName.addAll(getClassInDirectory(file));
            }
        }

        return allClassName;
    }

    private Object creatBean(String beanName) {
        BeanDefination beanDefination = beanDefinationHashMap.get(beanName);
        try {
            Object o = beanDefination.getaClass().getDeclaredConstructor().newInstance();
            return o;
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
    public Object getBean(String beanName){
        BeanDefination beanDefination = beanDefinationHashMap.get(beanName);
        if("singleton".equals(beanDefination.getScoped())){
            return singletonHashMap.get(beanName);
        }else{
            return creatBean(beanName);
        }
    }
}
