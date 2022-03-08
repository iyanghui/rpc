package pers.zhixilang.lego.rpc.netty.provider;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import pers.zhixilang.lego.rpc.netty.codec.json.JSONDecoder;
import pers.zhixilang.lego.rpc.netty.codec.json.JSONEncoder;

/**
 * 开启netty服务监听
 * 将服务地址添加到zookeeper数据节点
 *
 * @author zhixilang
 * @version 1.0
 * @date 2019-02-27 17:05
 */
public class NettyServer implements InitializingBean, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private static DefaultThreadFactory masterFactory = new DefaultThreadFactory("master-proxy", false);
    private static DefaultThreadFactory workerFactory = new DefaultThreadFactory("worker-proxy", false);

    /**
     * Reactor线程池
     * 用于接收客户端的tcp连接
     */
    private static final EventLoopGroup bossGroup = new NioEventLoopGroup(1, masterFactory);

    /**
     * Reactor线程池
     * 用于处理I/O相关的读写操作，或者执行系统task、定时任务等
     */
    private static final EventLoopGroup workerGroup = new NioEventLoopGroup(4, workerFactory);

    @Value("${rpc.provider}")
    private String rpcProviderUrl;

    /**
     * 启动
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        final  NettyServerHandler serverHandler = new NettyServerHandler();

        new Thread(() -> {
            try {
                // 初始化netty服务器，并且开始监听端口的socket请求
                ServerBootstrap serverBootstrap = new ServerBootstrap();
                serverBootstrap.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .option(ChannelOption.SO_BACKLOG, 1024)
                        .childOption(ChannelOption.SO_KEEPALIVE, true)
                        .childOption(ChannelOption.TCP_NODELAY, true)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            // 创建NIOSocketChannel成功后，在进行初始化时，将它的ChannelHandler设置到ChannelPipeline中，用于处理网络IO事件
                            @Override
                            protected void initChannel(SocketChannel channel) throws Exception {
                                ChannelPipeline pipeline = channel.pipeline();
                                pipeline.addLast(new IdleStateHandler(0, 0, 60));
                                pipeline.addLast(new JSONEncoder());
                                pipeline.addLast(new JSONDecoder());
                                pipeline.addLast(serverHandler);
                            }
                        });
                String[] arr = rpcProviderUrl.split(":");
                String host = arr[0];
                int port = Integer.parseInt(arr[1]);

                ChannelFuture cf = serverBootstrap.bind(host, port).sync();

                logger.info("rpc服务端启动,监听端口: " + port);

                cf.channel().closeFuture().sync();

            } catch (Exception e) {
                e.printStackTrace();

                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
        }).start();
    }

    @Override
    public void destroy() throws Exception {
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }
}
