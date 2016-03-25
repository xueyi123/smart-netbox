package com.iih5.netbox.codec;

import com.iih5.netbox.core.*;
import com.iih5.netbox.message.ByteMessage;
import com.iih5.netbox.message.JsonMessage;
import com.iih5.netbox.message.ProtoMessage;
import com.iih5.netbox.message.StringMessage;
import com.iih5.netbox.session.ISession;
import com.iih5.netbox.session.SessionManager;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.apache.log4j.Logger;

import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by XUEYI on 2016/3/25.
 */
public class WebSocketDecoder {
    private Logger logger = Logger.getLogger(WebSocketDecoder.class);

    public static void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        /**
         ** 包格式：包长度(int=4)+消息码(short=2)+数据段(byte[])
         1）包长度      :表示该整个数据的长度(包含用于表示长度本身的字节)
         2）消息码      :表示该数据包类型
         3）数据段      :采用byte[]形式存放
         */
        ByteBuf content =in;
        int packLen=content.readInt(); //packLen
        short msgId = content.readShort(); //cmdID
        ByteBuf msgBuf = Unpooled.buffer(content.readableBytes());//content
        content.readBytes(msgBuf);
        switch (GlobalConstant.messageType){
            case MessageType.BYTE_TYPE:
                out.add(new ByteMessage(msgId,msgBuf));
                break;
            case MessageType.JSON_TYPE:
                out.add(new JsonMessage(msgId,new String(content.array(), Charset.forName("UTF-8"))));
                break;
            case MessageType.PROTO_TYPE:
                out.add(new ProtoMessage(msgId,content.array()));
                break;
            case MessageType.STRING_TYPE:
                out.add(new StringMessage(msgId,new String(content.array(), Charset.forName("UTF-8"))));
                break;
        }












        byte headFlag=content.readByte(); //headFlag
        if (headFlag!=ProtocolConstant.PACK_HEAD_FLAG){
            if (GlobalConstant.debug){
                logger.error("检测到包头标识错误，正确包头应为："+ProtocolConstant.PACK_HEAD_FLAG);
            }else {
                ctx.disconnect().channel().close();
            }
        }
        int packLen=content.readInt(); //packLen
        short msgId = content.readShort(); //cmdID
        byte encr=content.readByte(); //encr
        ByteBuf msgBuf = Unpooled.buffer(content.readableBytes());//content
        content.readBytes(msgBuf);

        final Channel channel = ctx.channel();
        final AnnObject cmdHandler = CmdHandlerCache.getInstance().getAnnObject(msgId);
        final ISession session = SessionManager.getInstance().getSession(channel);

        switch (GlobalConstant.messageType){
            case MessageType.BYTE_TYPE:
                message = new ByteMessage(msgId,msgBuf);
                break;
            case MessageType.JSON_TYPE:
                message = new JsonMessage(msgId,new String(content.array(), Charset.forName("UTF-8")));
                break;
            case MessageType.PROTO_TYPE:
                message = new ProtoMessage(msgId,content.array());
                break;
            case MessageType.STRING_TYPE:
                message = new StringMessage(msgId,new String(content.array(), Charset.forName("UTF-8")));
                break;

        }
    }
}
