
package com.iih5.netbox.websocket;

import com.iih5.actor.IActor;
import com.iih5.netbox.NetBoxEngine;
import com.iih5.netbox.codec.ws.WsBinaryDecoder;
import com.iih5.netbox.codec.ws.WsTextDecoder;
import com.iih5.netbox.core.AnnObject;
import com.iih5.netbox.core.CmdHandlerCache;
import com.iih5.netbox.core.ProtocolConstant;
import com.iih5.netbox.message.Message;
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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

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
            BinaryWebSocketFrame bw = (BinaryWebSocketFrame) frame;
            ByteBuf content = bw.content();
            List<Object> list = new ArrayList<Object>(1);

            WsBinaryDecoder decoder= (WsBinaryDecoder) ProtocolConstant.wsBinaryDecoder.getClass().newInstance();
            Method method =decoder.getClass().getMethod("decode",Channel.class,ByteBuf.class,List.class);
            method.invoke(decoder,ctx.channel(),content,list);

            final Message message = (Message) list.get(0);
            final Channel channel = ctx.channel();
            final AnnObject cmdHandler = CmdHandlerCache.getInstance().getAnnObject(message.getId());
            final ISession session = SessionManager.getInstance().getSession(channel);
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
            String request = ((TextWebSocketFrame) frame).text();
            List<Object> list = new ArrayList<Object>(1);

            WsTextDecoder decoder= (WsTextDecoder) ProtocolConstant.wsTextDecoder.getClass().newInstance();
            Method method =decoder.getClass().getMethod("decode",Channel.class,String.class,List.class);
            method.invoke(decoder,ctx.channel(),request,list);

            final Message message = (Message) list.get(0);
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
