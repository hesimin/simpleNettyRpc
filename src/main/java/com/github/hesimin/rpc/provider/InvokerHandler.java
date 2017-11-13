package com.github.hesimin.rpc.provider;

import com.github.hesimin.rpc.common.RpcRequest;
import com.github.hesimin.rpc.common.RpcResponse;
import com.github.hesimin.rpc.provider.scan.ClassScan;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
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
        RpcResponse response = new RpcResponse();
        response.setMessageId(request.getMessageId());

        List<Object> clazzs = null;//classMap.get(request.getClassName());
        if (clazzs == null) {
            try {
                clazzs = ClassScan.getBean(request.getClassName());
//                classMap.put(request.getClassName(), clazzs);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                response.setSuccess(false);
                response.setError(e);
                writeAndClose(ctx, response);
                return;
            }
        }

        Object clazz = clazzs.get(0);
        Method method = clazz.getClass().getMethod(request.getMethodName(), request.getParameterTypes());

        Object result = null;
        try {
            result = method.invoke(clazz, request.getParameters());
            response.setSuccess(true);
            response.setReponse(result);
        } catch (Throwable e) {
            response.setSuccess(false);
            response.setError(e);
        }
        writeAndClose(ctx, response);
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
