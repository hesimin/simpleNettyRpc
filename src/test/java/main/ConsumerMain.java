package main;

import com.github.hesimin.api.RpcService;
import com.github.hesimin.api.RpcServiceImpl;
import com.github.hesimin.rpc.consumer.proxy.RpcProxyFactory;

/**
 * @author hesimin 2017-11-13
 */
public class ConsumerMain {
    public static void main(String[] args) {
        RpcService rpcService = new RpcServiceImpl();
        rpcService = RpcProxyFactory.proxyBean(rpcService);

        String result = rpcService.say("rpc test.");
        System.out.println("请求结果：" + result);
    }
}
