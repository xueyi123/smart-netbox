/*
 * Copyright 2016 xueyi (1581249005@qq.com)
 *
 * The SmartORM Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.iih5.netbox.codec;

import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.MessageLite;
import com.iih5.netbox.NetBoxEngine;
import com.iih5.netbox.actor.IActor;
import com.iih5.netbox.core.*;
import com.iih5.netbox.message.*;
import com.iih5.netbox.session.ISession;
import com.iih5.netbox.session.Session;
import com.iih5.netbox.session.SessionManager;
import com.iih5.netbox.util.ConsoleUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;

import java.nio.charset.Charset;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaders.Names.HOST;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {
    private static Logger logger =Logger.getLogger(WebSocketServerHandler.class);
    private static final String WEBSOCKET_PATH = "/websocket";

    private WebSocketServerHandshaker handshaker;

    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        //连接成功,绑定工作线程
        IActor actor = SessionManager.getInstance().createActor();
        Session session = new Session(ctx.channel(), actor);
        SessionManager.getInstance().addSession(ctx.channel(), session);
        if (NetBoxEngine.extension != null) {
            NetBoxEngine.extension.connect(session);
        }

    }

    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //断开连接,解除工作线程
        ISession session = SessionManager.getInstance().getSession(ctx.channel());
        if (NetBoxEngine.extension != null) {
            NetBoxEngine.extension.disConnect(session);
        }
        SessionManager.getInstance().removeSession(ctx.channel());
    }

    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    public void channelRead0(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof FullHttpRequest) {
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            try {
                handleWebSocketFrame(ctx, (WebSocketFrame) msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
        if (!req.getDecoderResult().isSuccess()) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST));
            return;
        }
        if (req.getMethod() != GET) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN));
            return;
        }
        if ("/".equals(req.getUri())) {
            ByteBuf content = TestPage.getContent(getWebSocketLocation(req));
            FullHttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, OK, content);

            res.headers().set(CONTENT_TYPE, "text/html; charset=UTF-8");
            HttpHeaders.setContentLength(res, content.readableBytes());

            sendHttpResponse(ctx, req, res);
            return;
        }
        if ("/favicon.ico".equals(req.getUri())) {
            FullHttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND);
            sendHttpResponse(ctx, req, res);
            return;
        }
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(getWebSocketLocation(req), null, false);
        handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), req);
        }
    }

    private static void sendHttpResponse(
            ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse res) {
        if (res.getStatus().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.getStatus().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
            HttpHeaders.setContentLength(res, res.content().readableBytes());
        }
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (!HttpHeaders.isKeepAlive(req) || res.getStatus().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    private static String getWebSocketLocation(FullHttpRequest req) {
        String location = req.headers().get(HOST) + WEBSOCKET_PATH;
        return "ws://" + location;
    }
    Message message=null;
    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {

        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        if (frame instanceof PongWebSocketFrame) {
            ctx.channel().write(new PingWebSocketFrame(frame.content().retain()));
            return;
        }
        if (frame instanceof BinaryWebSocketFrame) {
            /**
             ** 包格式：包头(byte=1)+包长度(int=4)+消息码(short=2)+加密段(byte=1)+数据段(byte[])
             1）包头        :表示数据包合法性
             2）包长度      :表示整个数据的长度(包含用于表示长度本身的字节)
             3）消息码      :表示数据包类型
             4）加密段      :表示数据段是否加密,采用什么加密算法
             5）数据段      :采用byte[]形式存放
             */
            BinaryWebSocketFrame bw = (BinaryWebSocketFrame) frame;
            ByteBuf content = bw.content();
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
            if (GlobalConstant.debug){
                logger.info("接收完整包数据信息《《 packSize:"+packLen+" msgId:"+msgId+" encr:"+encr+" data size="+msgBuf.array().length);
            }
            if (cmdHandler != null && session != null) {
                session.getActor().execute(new Runnable() {
                    public void run() {
                        try {
                            cmdHandler.getMethod().invoke(cmdHandler.getClas().newInstance(), message, session);
                        } catch (Exception e) {
                            logger.error("数据包分发错误： packSize:"+ ConsoleUtil.errException(e));
                        }
                    }
                });
            } else {
                logger.error("协议ID不存在，cmdId:" + message.getId());
                throw new UnsupportedOperationException("协议ID不存在，cmdId:" + message.getId());
            }
            return;
        }
        if (frame instanceof TextWebSocketFrame) {
            /**
             ** 包格式：包头(byte=1)+包长度(int=4)+消息码(short=2)+加密段(byte=1)+数据段(byte[])
             1）包头        :表示数据包合法性
             2）包长度      :表示整个数据的长度(包含用于表示长度本身的字节)
             3）消息码      :表示数据包类型
             4）加密段      :表示数据段是否加密,采用什么加密算法
             5）数据段      :采用byte[]形式存放
             */
            //text协议定义: "headFlag#packSize#id#encr#content",比如 0#0#10001#0#hello world!
            String request = ((TextWebSocketFrame) frame).text();
            String complex[];
            byte headFlag=0;
            int packSize=0;
            short msgId = 0;
            byte encr=0;
            String content = "";
            try {
                complex = request.split("#", 5);
                headFlag= Byte.valueOf(complex[0]);
                packSize=Integer.valueOf(complex[1]);
                msgId = Short.valueOf(complex[2]);
                encr=Byte.valueOf(complex[3]);
                content = complex[4];
            } catch (Exception e) {
                logger.error("协议格式不符合，正确的协议定义: headFlag#packSize#id#encr#content,比如 0#0#10001#0#hello world!");
                throw new UnsupportedOperationException("协议格式不符合，正确的协议定义:headFlag#packSize#id#encr#content,比如 0#0#10001#0#hello world!");
            }
            switch (GlobalConstant.messageType){
                case MessageType.JSON_TYPE:
                    message = new JsonMessage(msgId,content);
                    message.setEncryptType(encr);
                    break;
                case MessageType.STRING_TYPE:
                    message = new StringMessage(msgId,content);
                    message.setEncryptType(encr);
                    break;
                default:
                    logger.error("数据协议定义仅限制于JSON_TYPE 和STRING_TYPE");
                    throw new UnsupportedOperationException("数据协议定义仅限制于JSON_TYPE 和STRING_TYPE");
            }
            if (GlobalConstant.debug){
                logger.info("接收完整包数据信息《《 :"+request);
            }
            final Channel channel = ctx.channel();
            final AnnObject cmdHandler = CmdHandlerCache.getInstance().getAnnObject(message.getId());
            final ISession session = SessionManager.getInstance().getSession(channel);
            if (cmdHandler != null && session != null) {
                session.getActor().execute(new Runnable() {
                    public void run() {
                        try {
                            cmdHandler.getMethod().invoke(cmdHandler.getClas().newInstance(), message, session);
                        } catch (Exception e) {
                            logger.error("《《《数据分发错误："+ConsoleUtil.errException(e));
                        }
                    }
                });
            } else {
                logger.error("协议ID不存在，cmdId:" + message.getId());
                throw new UnsupportedOperationException("协议ID不存在，cmdId:" + message.getId());
            }
            return;
        }
    }
}
