package org.spring;

import javafx.beans.binding.ObjectExpression;
import org.spring.def.BeanDefination;
import org.spring.def.BeanDefinationBuilder;
import org.spring.util.BeanUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.print.DocFlavor;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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
    private Class configClass;
    //
    private String xmlfilePath;
    private final ConcurrentHashMap<String, Object> singletonHashMap = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, BeanDefination> beanDefinationHashMap = new ConcurrentHashMap<>();
    public SpringApplicationContext(Class configClass) throws IOException{
        scan(configClass);

        for(Map.Entry<String, BeanDefination> entry : beanDefinationHashMap.entrySet()){

            String beanId = entry.getKey();
            BeanDefination beanDefination = entry.getValue();
            if("singleton".equals(beanDefination.getScoped())){
                singletonHashMap.put(beanId, creatBean(beanId));
            }
        }
    }
    public SpringApplicationContext(String xmlfilePath) throws ParserConfigurationException, IOException, SAXException, ClassNotFoundException {
        scan(xmlfilePath);
        for(Map.Entry<String, BeanDefination> entry : beanDefinationHashMap.entrySet()){
            String beanId = entry.getKey();
            BeanDefination beanDefination = entry.getValue();
            if("singleton".equals(beanDefination.getScoped())){
                singletonHashMap.put(beanId, creatBean(beanId));
            }
        }
    }

    private NodeList readByXmlFile(String xmlfilePath) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document xmlDocument = builder.parse(xmlfilePath);

        xmlDocument.getDocumentElement().normalize();

        NodeList beanList = xmlDocument.getElementsByTagName("bean");

        return beanList;
    }
    private void scan(String xmlfilePath) throws ParserConfigurationException, IOException, SAXException, ClassNotFoundException {
        ClassLoader classLoader = SpringApplicationContext.class.getClassLoader();
        NodeList beanList = readByXmlFile(xmlfilePath);
        for(int i = 0; i < beanList.getLength(); i++){
            Node beanNode = beanList.item(i);
            if(beanNode.getNodeType() == Node.ELEMENT_NODE){
                Element beanEle = (Element) beanNode;
                BeanDefination beanDefination = BeanDefinationBuilder.genericBeanDefinationByXmlConfig(beanEle, classLoader);
                beanDefinationHashMap.put(beanEle.getAttribute("id"), beanDefination);
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
                String beanId = BeanUtil.getDefaultSpringBeanId(clazz);

                BeanDefination beanDefination = BeanDefinationBuilder.genericBeanDefinationByClassConfig(clazz);
                if(beanDefination != null) {
                    beanDefinationHashMap.put(beanId, beanDefination);
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

    private Object creatBean(String beanId) {
        BeanDefination beanDefination = beanDefinationHashMap.get(beanId);
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
