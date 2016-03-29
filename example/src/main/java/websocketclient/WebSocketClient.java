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
package websocketclient;

import com.iih5.netbox.message.Message;
import com.iih5.netbox.session.ISession;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;

import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketClient {
	private URI uri;
	private Bootstrap boost = new Bootstrap();
	public WebSocketClient(String url) {
		boost.group(new NioEventLoopGroup()).channel(NioSocketChannel.class)
		.handler(new ChannelInitializer<SocketChannel>() {
			@Override
			public void initChannel(SocketChannel ch) throws Exception {
				ChannelPipeline p = ch.pipeline();
				p.addLast(new WebSocketInitializer());
			}
		});
		try {
			uri = new URI(System.getProperty("url",url));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		connect(uri.getHost(), uri.getPort());
	}
	private void connect(String ip,int port) {
		try {		
			boost.connect(ip, port).sync();
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}	
	public void send(ISession session , Message netMessage) {
		ByteBuf byteBuf= Unpooled.buffer(netMessage.toArray().length+2);
		byteBuf.writeShort(netMessage.getId());
		byteBuf.writeBytes(netMessage.toArray());
		session.getChannel().writeAndFlush(new BinaryWebSocketFrame(byteBuf));
	}
	class WebSocketInitializer extends ChannelInitializer<SocketChannel> {
		@Override
		public void initChannel(SocketChannel ch) throws Exception {
			ChannelPipeline pipeline = ch.pipeline();
			pipeline.addLast(new HttpClientCodec());
			pipeline.addLast(new HttpObjectAggregator(65536));		
			WebSocketClientHandler wc=new WebSocketClientHandler(WebSocketClientHandshakerFactory.newHandshaker(uri,
					WebSocketVersion.V13, null, false, new DefaultHttpHeaders()));
			pipeline.addLast(wc);
		}
	}
}
