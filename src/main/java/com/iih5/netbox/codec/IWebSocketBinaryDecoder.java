package com.iih5.netbox.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import java.util.List;

/**
 * Created by XUEYI on 2016/3/25.
 */
public interface IWebSocketBinaryDecoder {
    /**
     * websocket解码
     * @param ctx
     * @param buffer
     * @param out
     */
    public void decode(Channel ctx, ByteBuf buffer, List<Object> out);
}
