package com.iih5.netbox.core;

import com.iih5.netbox.codec.tcp.TcpDecoder;
import com.iih5.netbox.codec.tcp.TcpEncoder;
import com.iih5.netbox.codec.tcp.TcpForDefaultByteDecoder;
import com.iih5.netbox.codec.tcp.TcpForDefaultByteEncoder;
import com.iih5.netbox.codec.ws.WsBinaryDecoder;
import com.iih5.netbox.codec.ws.WsBinaryEncoder;
import com.iih5.netbox.codec.ws.WsTextDecoder;
import com.iih5.netbox.codec.ws.WsTextEncoder;

/**
 * Created by XUEYI on 2016/3/18.
 */
public class ProtocolConstant {

    /**传输协类型*/
    public static int transformType= TransformType.TCP;

    //协议数据包头默认值 43   ...(^O^)
    public static byte PACK_HEAD_FLAG=43;

    //TCP编码/解密
    public static TcpEncoder tcpEncoder = new TcpForDefaultByteEncoder();
    public static TcpDecoder tcpDecoder = new TcpForDefaultByteDecoder();

    //WebSocket Text传输的编码/解码
    public static WsTextEncoder wsTextEncoder=null;
    public static WsTextDecoder wsTextDecoder=null;

    //WebSocket Binary传输的编码/解码
    public static WsBinaryEncoder wsBinaryEncoder=null;
    public static WsBinaryDecoder wsBinaryDecoder=null;

}
