package com.github.hesimin.api;

/**
 * @author hesimin 2017-11-13
 */
public class RpcServiceImpl implements RpcService {
    @Override
    public String say(String name,boolean createException) {
        if(createException){
            throw new RuntimeException("创建异常");
        }
        return "hello " + name;
    }
}
