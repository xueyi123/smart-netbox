package com.iih5.netbox.core;

import com.iih5.netbox.codec.IWebSocketBinaryDecoder;
import com.iih5.netbox.codec.IWebSocketBinaryEncoder;
import io.netty.channel.ChannelHandler;

/**
 * Created by XUEYI on 2016/3/18.
 */
public class ProtocolConstant {
    /**
     * 协议数据包头默认值 0x2B    ...(^O^)
     */
    public static byte PACK_HEAD_FLAG=0x2B;
    /**
     * 默认TCP编码/解密方式
     */
    public static ChannelHandler DEFAULT_TCP_CODEC[] = null;

    /**
     * 默认WebSocket编码/解密方式
     */
    public static ChannelHandler DEFAULT_WEB_SOCKET_CODEC = null;


    public static IWebSocketBinaryDecoder webSocketProtocolDecoder=null;

    public static IWebSocketBinaryEncoder webSocketProtocolEncoder=null;
}
