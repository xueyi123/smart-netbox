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
package com.iih5.example.client.tcp.byteclient;

import com.iih5.netbox.message.ByteMessage;
import com.iih5.netbox.message.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ByeClientHandler extends ChannelInboundHandlerAdapter{

	public void channelActive(ChannelHandlerContext ctx) throws Exception {

		System.err.println("建立连接。。。");

		for (int i = 0; i <10 ; i++) {
			String data= "|你好吗。SmartBoxServer....|";
			ByteMessage netMessage = new ByteMessage((short) 2001);
			netMessage.writeInt(110);
			netMessage.writeString("|helloworld|");
			netMessage.writeInt(119);
			ctx.channel().writeAndFlush(netMessage);
		}

	}
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
	  System.err.println("断开连接。。。");
	}
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
	
	}
	public void channelReadComplete(ChannelHandlerContext ctx)  throws Exception{
		ctx.flush();
	}
	public void channelRead(ChannelHandlerContext ctx, Object msg)  throws Exception{
		if (msg != null && msg instanceof Message) {
			Message netMsg = (Message) msg;
			System.err.println("接收数据： id="+netMsg.getId()+" msg="+netMsg.toString());
		}
	}
}



