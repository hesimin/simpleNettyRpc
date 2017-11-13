package com.github.hesimin.rpc.consumer.proxy;

import java.lang.reflect.Proxy;

/**
 * @author hesimin 2017-11-13
 */
public class RpcProxyFactory {
    public  static  <T> T proxyBean(Object target) {
        return (T) Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(), new RpcProxy());
    }
}
