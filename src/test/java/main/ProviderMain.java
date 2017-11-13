package main;

import com.github.hesimin.rpc.provider.RpcServer;

/**
 * @author hesimin 2017-11-13
 */
public class ProviderMain {
    public static void main(String[] args) {
        new RpcServer().start(8080);
    }
}
