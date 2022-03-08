package pers.zhixilang.lego.rpc.core.manager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhixilang
 * @version 1.0.0
 * date 2022-03-08 21:55
 */
public final class ServiceManager {
    private static Map<String, Object> serviceMap = new ConcurrentHashMap<>();

    public static void registryBean(String className, Object bean) {
        serviceMap.put(className, bean);
    }

    public static Object getBean(String className) {
        return serviceMap.get(className);
    }
}
