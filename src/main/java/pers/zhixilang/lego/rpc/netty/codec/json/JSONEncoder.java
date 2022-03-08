package pers.zhixilang.lego.rpc.netty.codec.json;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 *
 * @author zhixilang
 * @version 1.0
 * @date 2019-02-27 18:07
 */
public class JSONEncoder extends MessageToMessageEncoder {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object msg, List out) {
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.ioBuffer();
        byte[] bytes = JSON.toJSONBytes(msg);
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
        out.add(byteBuf);
    }

}
