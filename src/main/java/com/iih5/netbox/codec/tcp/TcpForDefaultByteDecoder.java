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
package com.iih5.netbox.codec.tcp;

import com.iih5.netbox.core.GlobalConstant;
import com.iih5.netbox.core.ProtocolConstant;
import com.iih5.netbox.message.ByteMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import org.apache.log4j.Logger;

import java.util.List;

public class TcpForDefaultByteDecoder extends TcpDecoder {
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
	private Logger logger = Logger.getLogger(TcpForDefaultByteDecoder.class);
	protected void decode(ChannelHandlerContext ctx, ByteBuf buffer,List<Object> out) throws Exception {
		if (GlobalConstant.debug){
			logger.info("收到数据《《 size:"+buffer.readableBytes());
		}
		if (buffer.readableBytes()<=0){
			return;
		}
		//开始读，做个标识，下次从这里开始
		buffer.markReaderIndex();
		//【1】包头判断，非法数据则清除，并考虑是否断掉连接
		byte header_name= buffer.readByte();
		if (header_name!= ProtocolConstant.PACK_HEAD_FLAG){
			if (GlobalConstant.debug){
				logger.error("连接被断开，检测到您包头标识错误:"+header_name+",正确包头应为:"+ProtocolConstant.PACK_HEAD_FLAG);
			}
			buffer.clear();//非法数据清除
			ctx.channel().disconnect().channel().close();
		}
		//【2】包长度判断，判断数据包长度是否达到基本长度，小于意味数据不完整则不处理，复位，等待下次数据到来时处理
		if (buffer.readableBytes()<HEAD_SIZE) {
			buffer.resetReaderIndex();
			return ;
		}
		//包长度(4个字节)
		int packSize = buffer.readInt();
		//消息号(2个字节)
		short msgId  = buffer.readShort();
		//加密段(1个字节)
		byte encr=buffer.readByte();
		//在读出包头后，还剩下实际数据长度的字节数
		int dataSize=packSize-HEAD_SIZE;
		//【2】数据段判断,数据段还不完整，复位，等待下次数据到来时处理
		if (buffer.readableBytes() < dataSize) {
			buffer.resetReaderIndex();
			return;
		}
		if (GlobalConstant.debug){
			logger.info("接收完整包数据信息《《 packSize:"+packSize+" msgId:"+msgId+" encr:"+encr+" data size="+dataSize);
		}
		// 数据对象组装
		ByteBuf b = Unpooled.buffer(dataSize);
		buffer.readBytes(b);
		ByteMessage message=new ByteMessage(msgId);
		message.setEncrypt(encr);
		message.setContent( b);
		out.add(message);
	}
}
