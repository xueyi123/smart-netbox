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
package example.client.websocket;

import com.iih5.netbox.message.StringMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.*;

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

    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel ch = ctx.channel();
        if (!handshaker.isHandshakeComplete()) {
            handshaker.finishHandshake(ch, (FullHttpResponse) msg);
            handshakeFuture.setSuccess();
            System.err.println("链接成功");
            //text测试
     //      ctx.channel().writeAndFlush(new TextWebSocketFrame("10001#0#你说这是什么问题啊啊啊啊啊啊12234ddggdfgdfgiiiiiiiiiiiiii"));
           //binary测试
    		for (int i = 0; i < 100; i++) {
				StringMessage netMessage = new StringMessage((short) 1001,"你好吗，，，，，，hello") ;
    			//ctx.channel().writeAndFlush(new TextWebSocketFrame(netMessage));
			}
    		
            return;
        }
        WebSocketFrame frame = (WebSocketFrame) msg;
    	if (frame instanceof PingWebSocketFrame) {
			ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
			return;
		}
//		if(frame instanceof BinaryWebSocketFrame) {
//			BinaryWebSocketFrame bw=(BinaryWebSocketFrame)frame;
//			ByteBuf conBuf = bw.content();
//			int cmdId=conBuf.readShort();
//		    ByteBuf msgBuf=Unpooled.buffer(conBuf.readableBytes());
//		    conBuf.readBytes(msgBuf);
//			StringMessage netMessage = new StringMessage((short) cmdId, msgBuf);
//		    System.err.println("<<<<<<<protoId="+cmdId+"   "+netMessage.toString());
//		}else {
//			TextWebSocketFrame f= (TextWebSocketFrame)frame;
//			System.err.println(f.text());
//		}
	}
}
