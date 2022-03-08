package pers.zhixilang.lego.rpc.core.client;

import org.springframework.beans.factory.FactoryBean;

import javax.annotation.Resource;
import java.lang.reflect.Proxy;

/**
 * 代理工厂
 * @author zhixilang
 * @version 1.0
 * @date 2019-03-03 20:12
 */
public class RpcFactoryBean<T> implements FactoryBean<T> {

    @Resource
    private RpcFactory rpcFactory;

    private Class<T> rpcInterface;

    public RpcFactoryBean() {}

    public RpcFactoryBean(Class<T> rpcInterface) {
        this.rpcInterface = rpcInterface;
    }

    @Override
    public T getObject() throws Exception {
        return (T) Proxy.newProxyInstance(rpcInterface.getClassLoader(), new Class[]{rpcInterface}, rpcFactory);
    }

    @Override
    public Class<?> getObjectType() {
        return rpcInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

 }
