/*
 * Copyright 2016 xueyi (1581249005@qq.com)
 *
 * The Smart-NetBox Project licenses this file to you under the Apache License,
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

import com.iih5.netbox.core.GlobalConstant;
import com.iih5.netbox.core.MessageType;
import com.iih5.netbox.message.ByteMessage;
import com.iih5.netbox.message.JsonMessage;
import com.iih5.netbox.message.ProtoMessage;
import com.iih5.netbox.message.StringMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.apache.log4j.Logger;

public class TcpProtocolEncoder extends MessageToByteEncoder<Object>{
	/**
	 ** 包格式：包长度(int)+消息码(short)+数据段(byte[])
	 1）包长度      :表示该整个数据的长度(包含用于表示长度本身的字节)
	 2）消息码      :表示该数据包类型
	 3）数据段      :采用byte[]形式存放
	 */
	//包长度,占用4个字节
	private final static int PACK_LEN = 4;
	//消息类型,占用2个字节
	private final static int TYPE_LEN = 2;
	//包头6个字节
	private final static int HEAD_SIZE =PACK_LEN+TYPE_LEN;
	private Logger logger = Logger.getLogger(TcpProtocolDecoder.class);
	protected void encode(ChannelHandlerContext ctx, Object message, ByteBuf out) throws Exception {
		if (GlobalConstant.debug){
			logger.info("编码发送数据包");
		}
		byte[] m=null;
		int packLen=0;
		short id=0;
		// 数据对象组装
		switch (GlobalConstant.messageType) {
			case MessageType.BYTE_TYPE:
				if (message instanceof ByteMessage) {
					ByteMessage byteMessage = (ByteMessage) message;
					m=byteMessage.toArray();
					packLen = m.length +HEAD_SIZE;
					id=byteMessage.getId();
				}
				break;
			case MessageType.JSON_TYPE:
				if (message instanceof JsonMessage) {
					JsonMessage jsonMessage = (JsonMessage) message;
					m = jsonMessage.toArray();
					packLen = m.length +HEAD_SIZE;
					id=jsonMessage.getId();
				}
				break;
			case MessageType.PROTO_TYPE:
				if (message instanceof ProtoMessage) {
					ProtoMessage protoMessage = (ProtoMessage) message;
					m = protoMessage.toArray();
					packLen = m.length +HEAD_SIZE;
					id=protoMessage.getId();
				}
				break;
			case MessageType.STRING_TYPE:
				if (message instanceof StringMessage) {
					StringMessage stringMessage = (StringMessage) message;
					m = stringMessage.toArray();
					packLen = m.length +HEAD_SIZE;
					id=stringMessage.getId();
				}
				break;
		}
		if (packLen>0){
			out.writeInt(packLen);
			out.writeShort(id);
			out.writeBytes(m);
			if (GlobalConstant.debug){
				logger.info("发送完整包数据信息》》 packSize:"+packLen+" msgId:"+id+" data size="+m.length);
			}
		}
	}
}
