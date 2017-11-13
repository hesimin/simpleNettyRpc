package com.github.hesimin.rpc.consumer;

import com.github.hesimin.rpc.common.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hesimin 2017-11-13
 */
public class ResultHandler extends ChannelInboundHandlerAdapter {
    private static final int                      TIME_OUT    = 10 * 1000;
    private              Map<String, RpcResponse> responseMap = new ConcurrentHashMap<>();


    public RpcResponse getResponse(String messageId) {
        RpcResponse response = responseMap.get(messageId);
        for (int i = 0; response == null && i <= 2000; i++) {
            try {
                Thread.sleep(50);
                response = responseMap.get(messageId);
            } catch (InterruptedException e) {
            }
        }
        responseMap.remove(messageId);

        return response;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcResponse response = (RpcResponse) msg;
        responseMap.put(response.getMessageId(), response);
        System.out.println("接受数据： msg = [" + msg + "]");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("client has Exception : ctx = [" + ctx + "], cause = [" + cause + "]");
        ctx.close();
    }
}
