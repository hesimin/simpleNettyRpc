package com.github.hesimin.rpc.provider;

import com.github.hesimin.rpc.common.RpcRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hesimin 2017-11-12
 */
public class InvokerHandler extends ChannelInboundHandlerAdapter {
    public static Map<String, Object> classMap = new ConcurrentHashMap<>();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcRequest request = (RpcRequest) msg;
        Object clazz = classMap.get(request.getClassName());
        if (clazz == null) {
            try {
                clazz = Class.forName(request.getClassName()).newInstance();
                classMap.put(request.getClassName(), clazz);
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                e.printStackTrace();
                writeAndClose(ctx, "Not found Class: " + request.getClassName());
                return;
            }
        }

        Method method = clazz.getClass().getMethod(request.getMethodName(), request.getParameterTypes());
        Object result = method.invoke(clazz, request.getParameters());
        writeAndClose(ctx, result);
    }

    private void writeAndClose(ChannelHandlerContext ctx, Object result) {
        ctx.write(result);
        ctx.flush();
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
