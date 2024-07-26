package org.settings.service;


import org.settings.service.impl.HelloServiceImpl;
import org.spring.Component;
import org.spring.Scoped;

@Component("helloService")
@Scoped()
public class HelloService implements HelloServiceImpl {
    public void hello(){
        System.out.println("Hello, World !");
    }
}
