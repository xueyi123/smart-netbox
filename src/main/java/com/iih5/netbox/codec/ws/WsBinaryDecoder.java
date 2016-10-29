package com.iih5.netbox.codec.ws;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import java.util.List;

public abstract class WsBinaryDecoder {
    public abstract void decode(Channel ctx, ByteBuf buffer, List<Object> out);
}
