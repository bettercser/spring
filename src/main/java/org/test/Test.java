package org.test;

import org.settings.AppConfig;
import org.spring.SpringApplicationContext;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException, ClassNotFoundException {
        SpringApplicationContext springApplicationContext = new SpringApplicationContext(AppConfig.class);
        System.out.println(springApplicationContext.getBean("helloService"));
        System.out.println(springApplicationContext.getBean("helloService"));
        System.out.println(springApplicationContext.getBean("helloService"));



        SpringApplicationContext springApplicationContextXml = new SpringApplicationContext("src/main/java/bean.xml");
        System.out.println(springApplicationContextXml.getBean("helloService"));
        System.out.println(springApplicationContextXml.getBean("helloService"));
        System.out.println(springApplicationContextXml.getBean("helloService"));
        System.out.println(springApplicationContextXml.getBean("helloController"));
        System.out.println(springApplicationContextXml.getBean("helloController"));
        System.out.println(springApplicationContextXml.getBean("helloController"));
    }
}
