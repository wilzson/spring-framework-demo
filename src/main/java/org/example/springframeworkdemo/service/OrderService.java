package org.example.springframeworkdemo.service;


import org.example.springframeworkdemo.spring.Autowired;
import org.example.springframeworkdemo.spring.Component;

@Component
public class OrderService {

    @Autowired
    private UserService userService;


    public void test01() {
        System.out.println(userService);
    }


//    public OrderService(UserService userService) {
//        this.userService = userService;
//    }
}
