package com.github.hesimin.api;

/**
 * @author hesimin 2017-11-13
 */
public class RpcServiceImpl implements RpcService {
    @Override
    public String say(String name) {
        return "hello " + name;
    }
}
