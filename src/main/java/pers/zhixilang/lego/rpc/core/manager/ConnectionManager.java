package pers.zhixilang.lego.rpc.core.manager;

import com.alibaba.fastjson.JSON;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.zhixilang.lego.rpc.common.Request;
import pers.zhixilang.lego.rpc.common.Response;
import pers.zhixilang.lego.rpc.netty.client.NettyClient;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhixilang
 * @version 1.0
 * @date 2019-03-03 20:36
 */
public class ConnectionManager {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionManager.class);

    private static NettyClient nettyClient;

    private static AtomicInteger roundRobin = new AtomicInteger(0);

    /**
     * 管理socketChannel(信道)
     */
    private static CopyOnWriteArrayList<Channel> channels = new CopyOnWriteArrayList<>();

    /**
     * 管理服务端地址
     */
    private static Map<SocketAddress, Channel> channelNodes = new ConcurrentHashMap<>();

    /**
     * message queue
     */
    private static ConcurrentHashMap<String, SynchronousQueue<Object>> messageQueueMap = new ConcurrentHashMap<>();

    public static void registry(NettyClient client) {
        nettyClient = client;
    }

    public static synchronized void updateConnectServer(List<String> addressList) {
        if (addressList == null || addressList.size() == 0) {
            logger.info("全部服务节点已关闭");

            for (final Channel channel: channels) {
                SocketAddress remotePeer = channel.remoteAddress();

                Channel node = channelNodes.get(remotePeer);
                node.close();
            }
            channels.clear();
            channelNodes.clear();
            return;
        }

        HashSet<SocketAddress> newAllServerNodeSet = new HashSet<>();
        for (String s : addressList) {
            String[] arr = s.split(":");
            if (arr.length == 2) {
                String host = arr[0];
                int post = Integer.parseInt(arr[1]);
                final SocketAddress socketAddress = new InetSocketAddress(host, post);
                newAllServerNodeSet.add(socketAddress);
            }
        }

        for (final SocketAddress socketAddress: newAllServerNodeSet) {
            Channel channel = channelNodes.get(socketAddress);
            if (channel == null || !channel.isOpen()) {
                connectServerNode(socketAddress);
            }
        }
    }

    /**
     * 简易版负载均衡,轮询调用
     * @return
     */
    public static Channel chooseChannel() {
        if (channels.size() > 0) {
            int size = channels.size();
            int index = (roundRobin.getAndAdd(1) + size) % size;
            return channels.get(index);
        } else {
            return null;
        }
    }

    public static void removeChannel(Channel channel) {
        SocketAddress address = channel.remoteAddress();
        channels.remove(channel);
        channelNodes.remove(address);
        logger.info("从连接管理器中移除channel.{}", address);
    }


    public static SynchronousQueue<Object> sendReq(Request request, Channel channel) {
        SynchronousQueue<Object> queue = new SynchronousQueue<>();
        messageQueueMap.put(request.getId(), queue);
        channel.writeAndFlush(request);
        return queue;
    }

    public static void refreshRes(Object msg) throws InterruptedException {
        Response response = JSON.parseObject(msg.toString(), Response.class);

        String requestId = response.getRequestID();

        SynchronousQueue<Object> queue = messageQueueMap.get(requestId);
        queue.put(response);
        messageQueueMap.remove(requestId);
    }

    private static void connectServerNode(SocketAddress socketAddress) {
        try {
            Channel channel = nettyClient.doConnect(socketAddress);
            addChannel(channel, socketAddress);
        } catch (Exception e) {
            logger.error("未能连接到服务器.{}", socketAddress);
        }
    }

    private static void addChannel(Channel channel, SocketAddress socketAddress) {
        channels.add(channel);
        channelNodes.put(socketAddress, channel);
        logger.info("channel{}加入中连接管理器", socketAddress);
    }
}
