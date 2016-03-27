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
package com.iih5.example.client.websocket;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.*;

import java.io.UnsupportedEncodingException;

public class WebSocketClientHandler extends SimpleChannelInboundHandler<Object> {

    private WebSocketClientHandshaker handshaker;
    private ChannelPromise handshakeFuture;

    public WebSocketClientHandler(WebSocketClientHandshaker handshaker) {
        this.handshaker = handshaker;

    }

    public void handlerAdded(ChannelHandlerContext ctx) {
        handshakeFuture = ctx.newPromise();
    }

    public void channelActive(ChannelHandlerContext ctx) {
        handshaker.handshake(ctx.channel());
    }

    public void channelInactive(ChannelHandlerContext ctx) {
        System.err.println("断开连接。。。");
    }

    private  void writeString(String str,ByteBuf buf) throws UnsupportedEncodingException {
        buf.writeShort(str.getBytes().length);
        buf.writeBytes(str.getBytes("UTF-8"));
    }

    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel ch = ctx.channel();
        if (!handshaker.isHandshakeComplete()) {
            handshaker.finishHandshake(ch, (FullHttpResponse) msg);
            handshakeFuture.setSuccess();
            System.err.println("链接成功");
            //text测试
//            for (int i = 0; i < 10; i++) {
//                sendText((short) 4001,"你说这是什么问题啊啊啊啊啊啊",ch);
//            }
            //binary测试
            for (int i = 0; i < 10; i++) {
                ByteBuf byteBuf =Unpooled.buffer();
                byteBuf.writeInt(3000008);
                byteBuf.writeDouble(1001);
                writeString("| hello world |",byteBuf);
                byteBuf.writeFloat(888.05f);
                sendBinary((short) 2001,byteBuf,ch);
            }
            return;
        }
        WebSocketFrame frame = (WebSocketFrame) msg;
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
		if(frame instanceof BinaryWebSocketFrame) {
			BinaryWebSocketFrame bw=(BinaryWebSocketFrame)frame;
			ByteBuf conBuf = bw.content();
            int len=conBuf.readInt();
			short cmdId=conBuf.readShort();
		    ByteBuf msgBuf=Unpooled.buffer(conBuf.readableBytes());
		    conBuf.readBytes(msgBuf);
		    System.err.println("<<<<<<<protoId="+cmdId+"   ");
		}else {
			TextWebSocketFrame f= (TextWebSocketFrame)frame;
			System.err.println(f.text());
		}
    }
    public void sendText(short msgId,String str,Channel ch ){
        //数据格式：msgId#text
        ch.writeAndFlush(new TextWebSocketFrame(msgId+"#"+str));
    }
    public  void sendBinary(short msgId, ByteBuf buf, Channel ch){
        //包格式：包长度(int)+消息码(short)+数据段(byte[])
        int len=buf.array().length+7;
        ByteBuf byteBuf= Unpooled.buffer(len);
        byteBuf.writeByte(43);
        byteBuf.writeInt(len);
        byteBuf.writeShort(msgId);
        byteBuf.writeByte(0);
        byteBuf.writeBytes(buf);
        ch.writeAndFlush(new BinaryWebSocketFrame(byteBuf));
    }
}
