package com.iih5.netbox.codec.ws;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

public abstract class WsBinaryEncoder {
    public abstract void encode(Channel ctx, Object message, ByteBuf out);
}
