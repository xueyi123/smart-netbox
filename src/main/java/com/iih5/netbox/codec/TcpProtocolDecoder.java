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

import com.google.protobuf.MessageLite;
import com.iih5.netbox.core.GlobalConstant;
import com.iih5.netbox.core.MessageType;
import com.iih5.netbox.message.ByteMessage;
import com.iih5.netbox.message.JsonMessage;
import com.iih5.netbox.message.ProtoMessage;
import com.iih5.netbox.message.StringMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.Charset;
import java.util.List;

public class TcpProtocolDecoder extends ByteToMessageDecoder{

	//包字节长度,占用4个字节
	private final static int PACK_LEN = 4;
	//消息类型,占用2个字节
	private final static int TYPE_LEN = 2;
	//包头6个字节
	private final static int HEAD_SIZE =PACK_LEN+TYPE_LEN;
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buffer,List<Object> out) throws Exception {
		/**
		 ** 包格式：包长度(int)+消息码(short)+数据段(byte[])
		        1）包长度      :表示该整个数据的长度(包含用于表示长度本身的字节)
				2）消息码      :表示该数据包类型
				3）数据段      :采用byte[]形式存放
		 */
		// 判断数据包长度是否达到基本长度，小于意味数据不完整则不处理，等待下次数据到来时处理
		if (buffer.readableBytes()<HEAD_SIZE) {
			return ;
		}
		buffer.markReaderIndex();//mark从头开始
		int   packSize = buffer.readInt();   // 包长度(4个字节)
		short msgId  = buffer.readShort(); // 消息号(2个字节)
		// 在读出包头后，还剩下实际数据长度的字节数
		int dataSize=packSize-HEAD_SIZE;
		if (buffer.readableBytes() < dataSize) {
			//数据包还不完整，复位，等待下次数据到来时处理
			buffer.resetReaderIndex();
			return;
		}
		// 数据对象组装
		ByteBuf b = Unpooled.buffer(dataSize);
		switch (GlobalConstant.messageType) {
			case MessageType.BYTE_TYPE:
				buffer.readBytes(b);
				out.add(new ByteMessage(msgId,b));
				break;
			case MessageType.JSON_TYPE:
				buffer.readBytes(b);
				out.add(new JsonMessage(msgId,new String(b.array(),Charset.forName("UTF-8"))));
				break;
			case MessageType.PROTO_TYPE:
				buffer.readBytes(b);
				out.add(new ProtoMessage(msgId,b.array()));
				break;
			case MessageType.STRING_TYPE:
				buffer.readBytes(b);
				out.add( new StringMessage(msgId,new String(b.array(),Charset.forName("UTF-8"))));
				break;
		}
	}
}
