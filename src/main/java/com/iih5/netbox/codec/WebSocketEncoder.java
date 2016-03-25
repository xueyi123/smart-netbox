package com.iih5.netbox.codec;

import com.iih5.netbox.core.ProtocolConstant;
import com.iih5.netbox.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

/**
 * Created by XUEYI on 2016/3/25.
 */
public class WebSocketEncoder  extends MessageToByteEncoder<Object> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        Message message = (Message) msg;
        int len=message.toArray().length+6;
        out.writeInt(len);
        out.writeShort(message.getId());
        out.writeBytes(message.toArray());

//        msg.resetReaderIndex();
//        int len=msg.toArray().length+7;
//        ByteBuf byteBuf= Unpooled.buffer(len);
//        byteBuf.writeByte(ProtocolConstant.PACK_HEAD_FLAG);
//        byteBuf.writeInt(len);
//        byteBuf.writeShort(msg.getId());
//        byteBuf.writeByte(msg.getEncryptType());
//        byteBuf.writeBytes(msg.toArray());
//        channel.writeAndFlush(new BinaryWebSocketFrame(byteBuf));


    }
}
