package pers.zhixilang.lego.rpc;

import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pers.zhixilang.lego.rpc.core.ServiceRegistry;
import pers.zhixilang.lego.rpc.core.client.RpcFactory;
import pers.zhixilang.lego.rpc.netty.client.NettyClient;
import pers.zhixilang.lego.rpc.netty.provider.NettyServer;
import pers.zhixilang.lego.rpc.core.ServiceDiscovery;
import pers.zhixilang.lego.rpc.core.provider.RpcProviderApplicationContextAware;

/**
 * @author zhixilang
 * @version 1.0.0
 * date 2022-03-08 20:01
 */
@Configuration
public class RpcAutoConfiguration {

    @Bean
    public RpcFactory rpcFactory() {
        return new RpcFactory();
    }

    @Bean
    public NettyClient nettyClient() {
        return new NettyClient();
    }

    @Bean
    public ServiceDiscovery serviceDiscovery() {
        return new ServiceDiscovery();
    }

    @Bean
    public ApplicationContextAware rpcProviderApplicationContextAware() {
        return new RpcProviderApplicationContextAware();
    }

    @Bean
    public NettyServer nettyServer() {
        return new NettyServer();
    }

    @Bean
    public ServiceRegistry serviceRegistry() {
        return new ServiceRegistry();
    }

}
