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

import com.iih5.netbox.core.GlobalConstant;
import com.iih5.netbox.core.MessageType;
import com.iih5.netbox.core.ProtocolConstant;
import com.iih5.netbox.message.ByteMessage;
import com.iih5.netbox.message.JsonMessage;
import com.iih5.netbox.message.ProtoMessage;
import com.iih5.netbox.message.StringMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.apache.log4j.Logger;

public class TcpProtocolEncoder2 extends MessageToByteEncoder<Object>{
	/**
	 ** 包格式：包头(byte=1)+包长度(int=4)+消息码(short=2)+加密段(byte=1)+数据段(byte[])
	 1）包头        :表示数据包合法性
	 2）包长度      :表示整个数据的长度(包含用于表示长度本身的字节)
	 3）消息码      :表示数据包类型
	 4）加密段      :表示数据段是否加密,采用什么加密算法
	 5）数据段      :采用byte[]形式存放
	 */
	//包头7个字节
	private final static int HEAD_SIZE =7;
	private Logger logger = Logger.getLogger(TcpProtocolDecoder2.class);
	protected void encode(ChannelHandlerContext ctx, Object message, ByteBuf out) throws Exception {
		if (GlobalConstant.debug){
			logger.info("编码发送数据包");
		}
		byte[] m=null;
		int packLen=0;
		short msgId=0;
		byte encr=0;
		// 数据对象组装
		switch (GlobalConstant.messageType) {
			case MessageType.BYTE_TYPE:
				if (message instanceof ByteMessage) {
					ByteMessage byteMessage = (ByteMessage) message;
					m=byteMessage.toArray();
					packLen = m.length +HEAD_SIZE;
					msgId=byteMessage.getId();
					encr=byteMessage.getEncryptType();
				}
				break;
			case MessageType.JSON_TYPE:
				if (message instanceof JsonMessage) {
					JsonMessage jsonMessage = (JsonMessage) message;
					m = jsonMessage.toArray();
					packLen = m.length +HEAD_SIZE;
					msgId=jsonMessage.getId();
					encr=jsonMessage.getEncryptType();
				}
				break;
			case MessageType.PROTO_TYPE:
				if (message instanceof ProtoMessage) {
					ProtoMessage protoMessage = (ProtoMessage) message;
					m = protoMessage.toArray();
					packLen = m.length +HEAD_SIZE;
					msgId=protoMessage.getId();
					encr=protoMessage.getEncryptType();
				}
				break;
			case MessageType.STRING_TYPE:
				if (message instanceof StringMessage) {
					StringMessage stringMessage = (StringMessage) message;
					m = stringMessage.toArray();
					packLen = m.length +HEAD_SIZE;
					msgId=stringMessage.getId();
					encr=stringMessage.getEncryptType();
				}
				break;
		}
		if (packLen>0){
			out.writeByte(ProtocolConstant.PACK_HEAD_FLAG);
			out.writeInt(packLen);
			out.writeShort(msgId);
			out.writeByte(encr);
			out.writeBytes(m);
			if (GlobalConstant.debug){
				logger.info("发送完整包数据信息》》 packSize:"+packLen+" msgId:"+msgId+" encr:"+encr+" data size="+m.length);
			}
		}
	}
}
