package pers.zhixilang.lego.rpc.core;

import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

/**
 * 服务注册
 * @author zhixilang
 * @version 1.0
 * @date 2019-02-27 17:11
 */
public class ServiceRegistry implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(ServiceRegistry.class);

    @Value("${rpc.registry}")
    private String rpcRegistryAddress;

    @Value("${rpc.provider}")
    private String rpcProviderUrl;

    private static final String ZK_REGISTRY_PATH = "/rpc";


    @Override
    public void afterPropertiesSet() throws Exception {
        if (null != rpcProviderUrl) {
            ZkClient client = connectServer();
            addRootNode(client);
            createNode(client, rpcProviderUrl);
        }
    }

    private ZkClient connectServer() {
        return new ZkClient(rpcRegistryAddress, 20000, 20000);
    }

    private void addRootNode(ZkClient client) {
        if (!client.exists(ZK_REGISTRY_PATH)) {
            client.createPersistent(ZK_REGISTRY_PATH);
            logger.info("创建zookeeper主节点：{}", ZK_REGISTRY_PATH);
        }
    }

    private void createNode(ZkClient client, String data) {
        // 创建临时顺序子节点
        // 保证生产者停掉之后，可以通知到消费者
        String path = client.create(ZK_REGISTRY_PATH + "/provider", data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        logger.info("创建zookeeper数据节点({} => {})", path, data);
    }
}
