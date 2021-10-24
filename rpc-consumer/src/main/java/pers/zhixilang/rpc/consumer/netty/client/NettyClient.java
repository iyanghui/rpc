package pers.zhixilang.rpc.consumer.netty.client;

import com.alibaba.fastjson.JSONObject;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pers.zhixilang.rpc.common.entity.Request;
import pers.zhixilang.rpc.common.entity.Response;
import pers.zhixilang.rpc.consumer.connection.ConnectionManage;
import pers.zhixilang.rpc.consumer.netty.codec.json.JSONDecoder;
import pers.zhixilang.rpc.consumer.netty.codec.json.JSONEncoder;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.net.SocketAddress;
import java.util.concurrent.SynchronousQueue;

/**
 * @author zhixilang
 * @version 1.0
 * @date 2019-03-03 20:32
 */
@Component
public class NettyClient {
    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private EventLoopGroup group = new NioEventLoopGroup(1);

    private Bootstrap bootstrap = new Bootstrap();

    @Resource
    private NettyClientHandler clientHandler;

    @Resource
    private ConnectionManage connectionManage;

    public NettyClient() {
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception{
                        ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast(new IdleStateHandler(0, 0, 30));
                        pipeline.addLast(new JSONEncoder());
                        pipeline.addLast(new JSONDecoder());
                        pipeline.addLast("handler", clientHandler);
                    }
                });
    }

    @PreDestroy
    public void destroy() {
        logger.info("rpc客户端退出");
        group.shutdownGracefully();
    }

    public Object send(Request request) throws InterruptedException {
        Channel channel = connectionManage.chooseChannel();
        if (null != channel && channel.isActive()) {
            SynchronousQueue<Object> queue = clientHandler.sendRequest(request, channel);
            // SynchronousQueue没有存储空间，内部使用transfer来实现take&put(offer&poll),
            // 可认为是长度为1的队列，
            // queue阻塞，直到queue.put()
            Object result = queue.take();
            return JSONObject.toJSONString(result);
        } else {
            Response response = new Response();
            response.setCode(-1);
            response.setMsg("未检测到服务器");
            return response;
        }
    }

    public Channel doConnect(SocketAddress socketAddress) throws Exception{
        // 考虑多连接
        ChannelFuture future = bootstrap.connect(socketAddress);
        return future.sync().channel();
    }
}
