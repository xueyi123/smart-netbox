package com.iih5.netbox.codec.ws;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

public abstract class WsTextEncoder {
    public abstract void encode(Channel ctx, Object msg, StringBuffer text);
}
