package com.github.hesimin.rpc.consumer.remote;

import com.github.hesimin.rpc.common.RpcRequest;
import com.github.hesimin.rpc.common.RpcResponse;
import com.github.hesimin.rpc.consumer.ResultHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hesimin 2017-11-13
 */
public class Client {
    private static final String HOST = "127.0.0.1";
    private static final int    PORT = 8080;

    private static final int WORKER_THREAD = 20;

    private EventLoopGroup worker;
    private Bootstrap      bootstrap;
    private Channel        channel;
    ResultHandler resultHandler = new ResultHandler();

    private static class Holder {
        private static Client client;

        static {
            client = new Client();
            client.connect();
        }

        private static Client instance() {
            return client;
        }
    }

    public void connect() {
        worker = new NioEventLoopGroup(WORKER_THREAD);
        bootstrap = new Bootstrap();

        bootstrap.group(worker)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    private int DATA_LENGTH = 4;

                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast(new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                        super.channelInactive(ctx);

                                    }
                                }) //处理分包传输问题
                                .addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, DATA_LENGTH, 0, DATA_LENGTH))
                                .addLast("frameEncoder", new LengthFieldPrepender(DATA_LENGTH))
                                .addLast("encoder", new ObjectEncoder())
                                .addLast("decoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)))
                                .addLast("handler", resultHandler);
                    }
                });
        channel = bootstrap.connect(HOST, PORT).syncUninterruptibly().channel();
        System.out.println("server start port:" + PORT);
    }

    public RpcResponse sent(final RpcRequest request) {
        channel.writeAndFlush(request);
        return resultHandler.getResponse(request.getMessageId());
    }

    public static Client instance() {
        return Holder.instance();
    }

    public void close() {
        if (channel != null) {
            worker.shutdownGracefully();
            channel.closeFuture().syncUninterruptibly();
        }
    }

}
