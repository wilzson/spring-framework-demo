package org.example.springframeworkdemo.service;

import org.example.springframeworkdemo.spring.ApplicationContext;

public class Test {
    public static void main(String[] args) {

        ApplicationContext context = new ApplicationContext(AppConfig.class);

        OrderService orderService = (OrderService) context.getBean("orderService");

        orderService.test01();
    }
}
