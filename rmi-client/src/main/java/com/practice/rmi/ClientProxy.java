package com.practice.rmi;

import java.lang.reflect.Proxy;

public class ClientProxy {

    public <T> T newProxy(Class<T> interfaceCls, String host, int port) {
        //使用到了动态代理。
        return (T)Proxy.newProxyInstance(interfaceCls.getClassLoader(),
                new Class[]{interfaceCls},new RemoteInvocationHandler(host,port));
    }

}
