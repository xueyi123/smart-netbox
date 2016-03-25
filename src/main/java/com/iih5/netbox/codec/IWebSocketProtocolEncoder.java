package com.iih5.netbox.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

/**
 * Created by XUEYI on 2016/3/25.
 */
public interface IWebSocketProtocolEncoder {
    public void encode(Channel ctx, Object message, ByteBuf out);
}
