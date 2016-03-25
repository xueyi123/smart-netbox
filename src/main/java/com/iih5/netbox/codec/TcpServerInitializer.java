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

import com.iih5.netbox.core.ProtocolConstant;
import com.iih5.netbox.core.TcpCodecType;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

public class TcpServerInitializer extends ChannelInitializer<SocketChannel>{
	private final int idleTime=7200;//2小时
	public ChannelHandler encoder;
	public ChannelHandler decoder;
	public TcpServerInitializer(ChannelHandler encoder,ChannelHandler decoder){
		this.encoder=encoder;
		this.decoder=decoder;
	}
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline p = ch.pipeline();
		//解码：将二进制数据（如ByteBuf）装换成Java对象
		p.addLast(decoder);
		//编码：将Java对象装换成二进制数据
		p.addLast(encoder);
		//空闲：超时检测处理Handler
		p.addLast(new IdleStateHandler(idleTime, 0, 0));
		//逻辑：执行业务逻辑
		p.addLast(new TcpStateHandler());
	}
}
