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
package com.iih5.netbox;

import com.iih5.netbox.actor.CurrentUtils;
import com.iih5.netbox.actor.QueueActorManager;
import com.iih5.netbox.annotation.InOut;
import com.iih5.netbox.annotation.Protocol;
import com.iih5.netbox.annotation.Request;
import com.iih5.netbox.core.*;
import com.iih5.netbox.session.SessionManager;
import com.iih5.netbox.tcp.TcpServerInitializer;
import com.iih5.netbox.util.ClassUtil;
import com.iih5.netbox.util.ConsoleUtil;
import com.iih5.netbox.websocket.WebSocketServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.log4j.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

public class NetBoxEngine {
	private static Logger logger = Logger.getLogger(NetBoxEngine.class);
	public static ConnectExtension extension=null;
	private NetBoxEngineSetting settings=null;
	public NetBoxEngine() {
		this.settings =new NetBoxEngineSetting();
	}
	public NetBoxEngineSetting getSettings() {
		return settings;
	}
	public void setSettings(NetBoxEngineSetting settings) {
		this.settings = settings;
	}
	public NetBoxEngine(NetBoxEngineSetting settings) {
		this.settings=settings;
	}
	/**启动网络服务*/
	public void start() {
		if (settings.getPlayerThreadSize()>0){
			SessionManager.getInstance().setActorManager(new QueueActorManager(settings.getPlayerThreadSize(), CurrentUtils.createThreadFactory("User-Pool-")));
		}else {
			logger.error("用户管理线程数量不能小于1个!");
			throw new UnsupportedOperationException("用户管理线程数量不能小于1个!");
		}
		EventLoopGroup bossGroup   = new NioEventLoopGroup(settings.getBossThreadSize());
		EventLoopGroup workerGroup = new NioEventLoopGroup(settings.getWorkerThreadSize());
		try {
			ServerBootstrap b = new ServerBootstrap();
			if (settings.getBasePackage()==null || settings.getBasePackage().equals("")) {
				logger.error("请设置协议映射扫描目录，比如 com.ab!");
				throw new UnsupportedOperationException("请设置协议映射扫描目录，比如 com.ab");
			}else {
				protocolMapping();
					if (ProtocolConstant.transformType==TransformType.WS_BINARY
						||ProtocolConstant.transformType==TransformType.WS_TEXT){
					b.group(bossGroup, workerGroup)
							.channel(NioServerSocketChannel.class)
							.childHandler(new WebSocketServerInitializer());
					b.bind(settings.getPort()).sync();
					logger.info("WebSocket port="+settings.getPort()+"启动成功！");
					logger.info("Open your web browser and navigate to http://127.0.0.1:" + settings.getPort() + '/');
				}else {
						b.group(bossGroup, workerGroup)
							.option(ChannelOption.TCP_NODELAY, true)
							.option(ChannelOption.SO_KEEPALIVE, true)
							.channel(NioServerSocketChannel.class)
							.childHandler(new TcpServerInitializer());
					ChannelFuture f=b.bind(settings.getPort()).sync();
					logger.info("TCP port="+settings.getPort()+"启动成功！");
				}
			}
		}catch (InterruptedException e) {
			logger.error("服务器关闭 shutdown！");
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}
	//映射扫描
	private void protocolMapping(){
		List<Class<?>> allClass = ClassUtil.getClasses(settings.getBasePackage());
		for (Class<?> class1 : allClass) {
			if (class1.isAnnotationPresent(Request.class)) {
				Method[] mds= class1.getDeclaredMethods();
				for (Method method : mds) {
					if (method.isAnnotationPresent(Protocol.class)) {
						Annotation[] ans= method.getAnnotations();
						for (Annotation annotation : ans) {
							Protocol methn = (Protocol) annotation;
							try {
								CmdHandlerCache.getInstance().putCmdHandler(methn.value(), new AnnObject(class1, method));
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			}else if (class1.isAnnotationPresent(InOut.class)) {
				if(extension!=null){
					throw new IllegalArgumentException("重复加载Extension( extension = "+class1.getName()+")");
				}else {
					try {
						Object obj = class1.newInstance();
						extension=(ConnectExtension)obj;
					} catch (InstantiationException e) {
						logger.error("加载Extension 出错："+ ConsoleUtil.errException(e));
					} catch (IllegalAccessException e) {
						logger.error("加载Extension 出错："+ ConsoleUtil.errException(e));
					}
				}
			}
		}
	}
	
}
