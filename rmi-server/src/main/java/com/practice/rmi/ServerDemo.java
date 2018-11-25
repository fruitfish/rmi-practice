package com.practice.rmi;

import java.io.IOException;

public class ServerDemo {


    public static void main(String[] args) throws IOException {
        IHelloService helloService=new HelloServiceImpl();
        RpcServer rpcServer=new RpcServer();
        rpcServer.publishService(helloService,8888);


    }
}
