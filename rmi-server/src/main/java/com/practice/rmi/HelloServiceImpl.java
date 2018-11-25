package com.practice.rmi;

public class HelloServiceImpl implements IHelloService {
    @Override
    public String sayHello(String msg) {
        System.out.println("some one say: " + msg);
        return  "some one say: " + msg;
    }
}
