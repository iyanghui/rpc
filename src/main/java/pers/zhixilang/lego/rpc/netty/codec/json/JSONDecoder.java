package pers.zhixilang.lego.rpc.netty.codec.json;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 *
 * @author zhixilang
 * @version 1.0
 * @date 2019-02-27 18:07
 */
public class JSONDecoder extends LengthFieldBasedFrameDecoder {
    public JSONDecoder() {
        super(65535, 0, 4, 0, 4);
    }

    @Override
    protected Object decode(ChannelHandlerContext context, ByteBuf in) throws Exception{
        ByteBuf decode = (ByteBuf) super.decode(context, in);
        if (decode == null){
            return null;
        }
        int dataLen = decode.readableBytes();
        byte[] bytes = new byte[dataLen];
        decode.readBytes(bytes);
        return JSON.parse(bytes);
    }
}
