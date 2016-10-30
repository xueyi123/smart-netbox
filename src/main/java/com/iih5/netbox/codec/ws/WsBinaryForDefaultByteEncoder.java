package com.iih5.netbox.codec.ws;

import com.iih5.netbox.codec.ws.WsBinaryEncoder;
import com.iih5.netbox.core.ProtocolConstant;
import com.iih5.netbox.message.ByteMessage;
import com.iih5.netbox.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

public class WsBinaryForDefaultByteEncoder extends WsBinaryEncoder{
    @Override
    public void encode(Channel ctx, Object message, ByteBuf out) {
        pack(message, out);
    }
    public void pack( Object message, ByteBuf out){
        ByteMessage msg = (ByteMessage) message;
        msg.resetReaderIndex();
        int len=msg.getContentArray().length+7;
        out.writeByte(ProtocolConstant.PACK_HEAD_FLAG);
        out.writeInt(len);
        out.writeShort(msg.getId());
        out.writeByte(msg.getEncrypt());
        out.writeBytes(msg.getContentArray());
    }
}
