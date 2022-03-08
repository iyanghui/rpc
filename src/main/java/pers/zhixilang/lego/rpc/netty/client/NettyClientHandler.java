package pers.zhixilang.lego.rpc.netty.client;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.zhixilang.lego.rpc.common.Request;
import pers.zhixilang.lego.rpc.core.manager.ConnectionManager;

import java.net.InetSocketAddress;

/**
 * @author zhixilang
 * @version 1.0
 * @date 2019-03-03 20:32
 */
@ChannelHandler.Sharable
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext context) {
        logger.info("已连接到rpc服务器{}", context.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext context) {
        InetSocketAddress socketAddress = (InetSocketAddress) context.channel().remoteAddress();

        logger.info("与rpc服务器断开连接.{}", socketAddress);
        context.channel().close();

        ConnectionManager.removeChannel(context.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext context, Object msg) throws Exception {
        ConnectionManager.refreshRes(msg);

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
