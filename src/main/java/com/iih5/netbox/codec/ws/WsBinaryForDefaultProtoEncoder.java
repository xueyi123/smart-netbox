package com.iih5.netbox.codec.ws;

import com.iih5.netbox.core.ProtocolConstant;
import com.iih5.netbox.message.ProtoMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

public class WsBinaryForDefaultProtoEncoder extends WsBinaryEncoder{
    @Override
    public void encode(Channel ctx, Object message, ByteBuf out) {
       pack(message, out);
    }
    public void pack(Object message, ByteBuf out){
        ProtoMessage msg = (ProtoMessage) message;
        int len=msg.toArray().length+7;
        out.writeByte(ProtocolConstant.PACK_HEAD_FLAG);
        out.writeInt(len);
        out.writeShort(msg.getId());
        out.writeByte(msg.getEncrypt());
        out.writeBytes(msg.toArray());
    }
}
