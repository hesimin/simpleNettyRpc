package com.github.hesimin.rpc.consumer.proxy;

import com.github.hesimin.rpc.common.RpcRequest;
import com.github.hesimin.rpc.common.RpcResponse;
import com.github.hesimin.rpc.consumer.remote.Client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * @author hesimin 2017-11-12
 */
public class RpcProxy implements InvocationHandler {
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest request = new RpcRequest();
        request.setMessageId(UUID.randomUUID().toString());
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(args);

        RpcResponse response = Client.instance().sent(request);
        if (response.isSuccess()) {
            return response.getReponse();
        } else {
            throw response.getError();
        }
    }
}
