/** 
 * ---------------------------------------------------------------------------   
 * 类名称   ：Client.java
 * 类描述   ：   
 * 创建人   ： 
 * 创建时间： 2015年4月14日 下午2:12:24      
 * 版权拥有：天天互动科技有限公司
 * --------------------------------------------------------------------------- 
 */ 
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
package com.iih5.example.client.tcp.jsonclient;

import com.iih5.netbox.codec.TcpProtocolDecoder;
import com.iih5.netbox.codec.TcpProtocolEncoder;
import com.iih5.netbox.core.GlobalConstant;
import com.iih5.netbox.message.Message;
import com.iih5.netbox.session.ISession;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Timer;
import java.util.TimerTask;

public class JsonClient {
	private Bootstrap boost = new Bootstrap();
	private String ip;
	private int port;
	public Timer timer = new Timer();
	public JsonClient() {
		boost.group(new NioEventLoopGroup()).channel(NioSocketChannel.class)
		.handler(new ChannelInitializer<SocketChannel>() {
			@Override
			public void initChannel(SocketChannel ch) throws Exception {
				ChannelPipeline p = ch.pipeline();
				p.addLast(new TCPInitializer());
			}
		});
	}
	public  void  setMessageType(int type){
		GlobalConstant.messageType=type;
	}
	/**
	 * 连接
	 * @param ip
	 * @param port
	 */
	public void connect(String ip,int port) {
		try {
			this.ip=ip;
			this.port=port;
			boost.connect(ip, port).sync();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("连接失败，继续尝试重新连接。。。");		
			reconnect();
		}
	}
	private void reconnect(){
		try {
			boost.connect(ip, port).sync();
		} catch (Exception e) {
			e.printStackTrace();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					System.err.println("连接失败，继续尝试重新连接。。。");		
					reconnect();
				}
			}, 8000);
		}
	}
	/**
	 * 发送消息（bytebuf传输）
	 * @param session
	 * @param netMessage
	 */
	public void send(ISession session , Message netMessage) {
		session.getChannel().writeAndFlush(netMessage);
	}
	class TCPInitializer  extends ChannelInitializer<SocketChannel>{
		@Override
		protected void initChannel(SocketChannel ch) throws Exception {
			ChannelPipeline p = ch.pipeline();
			p.addLast(new TcpProtocolDecoder());
			p.addLast(new TcpProtocolEncoder());
			p.addLast(new JsonClientHandler());
		}
	}
}

