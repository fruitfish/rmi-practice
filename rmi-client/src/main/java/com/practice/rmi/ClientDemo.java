package com.practice.rmi;

public class ClientDemo {


    public static void main(String[] args) {

        ClientProxy rpcClientProxy=new ClientProxy();

        IHelloService hello=rpcClientProxy.newProxy
                (IHelloService.class,"localhost",8888);
        System.out.println(hello.sayHello("mic"));

    }

}
