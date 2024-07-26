package org.test;

import org.settings.AppConfig;
import org.spring.SpringApplicationContext;

import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException {
        SpringApplicationContext springApplicationContext = new SpringApplicationContext(AppConfig.class);
        System.out.println(springApplicationContext.getBean("org.settings.service.HelloService"));
        System.out.println(springApplicationContext.getBean("org.settings.service.HelloService"));
        System.out.println(springApplicationContext.getBean("org.settings.service.HelloService"));
    }
}
