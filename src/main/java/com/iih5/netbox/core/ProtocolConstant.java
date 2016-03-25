package com.iih5.netbox.core;

import com.iih5.netbox.codec.IWebSocketProtocolDecoder;
import com.iih5.netbox.codec.IWebSocketProtocolEncoder;
import com.iih5.netbox.codec.TcpProtocolDecoder2;
import com.iih5.netbox.codec.TcpProtocolEncoder2;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.HttpServerCodec;

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


    public static IWebSocketProtocolDecoder webSocketProtocolDecoder=null;

    public static IWebSocketProtocolEncoder webSocketProtocolEncoder=null;
}
