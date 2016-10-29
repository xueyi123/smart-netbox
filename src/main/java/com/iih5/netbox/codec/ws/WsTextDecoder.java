package com.iih5.netbox.codec.ws;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import java.util.List;

public abstract class WsTextDecoder {
    public abstract void decode(Channel ctx, String text, List<Object> out);
}
