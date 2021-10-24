package pers.zhixilang.rpc.consumer.netty.client;

import com.alibaba.fastjson.JSON;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pers.zhixilang.rpc.common.entity.Request;
import pers.zhixilang.rpc.common.entity.Response;
import pers.zhixilang.rpc.consumer.connection.ConnectionManage;

import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;

/**
 * @author zhixilang
 * @version 1.0
 * @date 2019-03-03 20:32
 */
@Component
@ChannelHandler.Sharable
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);

    @Resource
    private ConnectionManage connectionManage;

    private ConcurrentHashMap<String, SynchronousQueue<Object>> queueMap = new ConcurrentHashMap<>();

    @Override
    public void channelActive(ChannelHandlerContext context) {
        logger.info("已连接到rpc服务器{}", context.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext context) {
        InetSocketAddress socketAddress = (InetSocketAddress) context.channel().remoteAddress();

        logger.info("与rpc服务器断开连接.{}", socketAddress);
        context.channel().close();

        connectionManage.removeChannel(context.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext context, Object msg) throws Exception {
        Response response = JSON.parseObject(msg.toString(), Response.class);

        String requestId = response.getRequestID();

        SynchronousQueue<Object> queue = queueMap.get(requestId);
        queue.put(response);
        queueMap.remove(requestId);

    }

    public SynchronousQueue<Object> sendRequest(Request request, Channel channel) {
        SynchronousQueue<Object> queue = new SynchronousQueue<>();
        queueMap.put(request.getId(), queue);
        channel.writeAndFlush(request);
        return queue;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext context, Object e) throws Exception{
        logger.info("已超过30秒未与rpc服务器通信！发送心跳信息.");

        if (e instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) e;

            if (event.state() == IdleState.ALL_IDLE) {
                Request request = new Request();
                request.setMethodName("heartBeat");
                context.channel().writeAndFlush(request);
            }
        } else {
            super.userEventTriggered(context, e);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable ex) {
        logger.info("rpc服务通信异常.", ex);
        context.channel().close();
    }


}
