
package com.iih5.netbox.codec.tcp;

import com.iih5.netbox.core.GlobalConstant;
import com.iih5.netbox.core.ProtocolConstant;
import com.iih5.netbox.message.ProtoMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpForDefaultProtoEncoder extends TcpEncoder {
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
	private Logger logger = LoggerFactory.getLogger(TcpForDefaultByteDecoder.class);
	protected void encode(ChannelHandlerContext ctx, Object message, ByteBuf out) throws Exception {
		pack(message, out);
	}
	public void pack(Object message, ByteBuf out){
		if (GlobalConstant.debug){
			logger.info("编码发送数据包");
		}
		byte[] m=null;
		int packLen=0;
		short msgId=0;
		byte encr=0;
		// 数据对象组装
		ProtoMessage byteMessage = (ProtoMessage) message;
		m=byteMessage.getMessage();
		packLen = m.length +HEAD_SIZE;
		msgId=byteMessage.getId();
		encr=byteMessage.getEncrypt();

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
