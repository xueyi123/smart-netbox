
package com.iih5.netbox.tcp;

import com.iih5.netbox.codec.tcp.TcpForDefaultByteDecoder;
import com.iih5.netbox.codec.tcp.TcpForDefaultByteEncoder;
import com.iih5.netbox.core.ProtocolConstant;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

public class TcpServerInitializer extends ChannelInitializer<SocketChannel>{
	private final int idleTime=7200;//2小时
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline p = ch.pipeline();
		//解码：将二进制数据（如ByteBuf）装换成Java对象
		p.addLast(ProtocolConstant.tcpDecoder.getClass().newInstance());
		//编码：将Java对象装换成二进制数据
		p.addLast(ProtocolConstant.tcpEncoder.getClass().newInstance());
		//空闲：超时检测处理Handler
		p.addLast(new IdleStateHandler(idleTime, 0, 0));
		//逻辑：执行业务逻辑
		p.addLast(new TcpStateHandler());


	}
}
