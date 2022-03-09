package pers.zhixilang.lego.rpc.core.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import pers.zhixilang.lego.rpc.core.manager.ServiceManager;
import pers.zhixilang.lego.rpc.annotation.RpcService;

import java.util.Map;

/**
 * @author zhixilang
 * @version 1.0.0
 * date 2022-03-08 21:54
 */
public class RpcProviderContextAware implements ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(RpcProviderContextAware.class);

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(RpcService.class);
        for (Object bean: beans.values()) {
            Class clazz = bean.getClass();
            Class[] interfaces = clazz.getInterfaces();
            for (Class inter: interfaces) {
                String interfaceName = inter.getName();
                logger.info("加载rpc service「{}」", interfaceName);
                ServiceManager.registryBean(interfaceName, bean);
            }
        }
    }
}
