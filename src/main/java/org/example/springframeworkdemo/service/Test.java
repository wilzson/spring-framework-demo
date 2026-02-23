package org.example.springframeworkdemo.service;

import org.example.springframeworkdemo.spring.ApplicationContext;

public class Test {
    public static void main(String[] args) {

        ApplicationContext context = new ApplicationContext(AppConfig.class);

        context.getBean("userService");
    }
}
