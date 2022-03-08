package pers.zhixilang.lego.rpc.core;

import com.alibaba.fastjson.JSONObject;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import pers.zhixilang.lego.rpc.core.manager.ConnectionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务发现
 * @author zhixilang
 * @version 1.0
 * @date 2019-03-03 20:36
 */
public class ServiceDiscovery implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(ServiceDiscovery.class);

    @Value("${rpc.registry}")
    private String rpcRegistryAddress;

    private volatile List<String> addressList = new ArrayList<>();

    private static final String ZK_REGISTRY_PATH = "/rpc";

    private ZkClient client;

    @Override
    public void afterPropertiesSet() throws Exception {
        client = connectServer();
        watchNode(client);
    }

    private ZkClient connectServer() {
        return new ZkClient(rpcRegistryAddress, 2000, 2000);
    }

    private void watchNode(ZkClient client) {
        List<String> nodeList = client.subscribeChildChanges(ZK_REGISTRY_PATH, (s, nodes) -> {
           logger.info("监听到节点数据变化{}", JSONObject.toJSONString(nodes));

           addressList.clear();
           getNodeData(nodes);
           updateConnectedServer();
        });

        getNodeData(nodeList);

        logger.info("已发现的服务列表: {}", JSONObject.toJSONString(nodeList));

        updateConnectedServer();
    }

    private void getNodeData(List<String> nodes) {
        for (String node: nodes) {
            String address = client.readData(ZK_REGISTRY_PATH + "/" + node);
            addressList.add(address);
            logger.info("/rpc节点 {} 的数据为 {} ", node, address);
        }
    }

    private void updateConnectedServer() {
        ConnectionManager.updateConnectServer(addressList);
    }
}
