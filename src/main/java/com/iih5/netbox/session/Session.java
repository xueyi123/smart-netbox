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
package com.iih5.netbox.session;

import com.iih5.netbox.actor.IActor;
import com.iih5.netbox.core.ProtocolConstant;
import com.iih5.netbox.core.TransformType;
import com.iih5.netbox.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;

public class Session implements ISession {
	private String sessionID;
	private Channel channel;
	private IActor actor;
	private String userId;
	private Info info;
	private Map<String, Object> parasMap=new Hashtable<String, Object>();
	public Session (Channel channel){
		this.channel=channel;
		sessionID = UUID.randomUUID().toString();
	}
	public Session (Channel channel,IActor actor) {
		this.channel=channel;
		this.actor=actor;
		sessionID = UUID.randomUUID().toString();
	}

	public Channel getChannel() {
		return channel;
	}

	public String  getId() {
		return sessionID;
	}

	public IActor getActor() {
		return actor;
	}

	public void setParameter(String key, Object value) {
		parasMap.put(key, value);
	}

	public boolean containParameter(String key) {
		return parasMap.containsKey(key);
	}

	public Object getParameter(String key) {
		return parasMap.get(key);
	}

	public void setParameters(Map<String, Object> vars) {
		parasMap.putAll(vars);
	}


	public void setInfo(Info info) {
		this.info=info;
	}

	public Info getInfo() {
		return info;
	}

	public void removeParameter(String key) {
		parasMap.remove(key);
	}

	public void clearParameters() {
		parasMap.clear();
	}

	public boolean bindUserID(String userId) {
		if (userId!=null){
			this.userId=userId;
			return SessionManager.getInstance().bindUserIDAndChannel(userId,channel);
		}
		return  false;
	}

	public void unBindUserID(String userId) {
		if (userId!=null){
			this.userId=null;
			SessionManager.getInstance().unBindUserIDAndChannel(userId);
		}
	}

	public String getUserID() {
		return this.userId;
	}
	/**
	 * 发送tcp/websocket binary传输
	 * @param msg
     */
	public void send(Message msg) {
		if (channel!=null) {
			if (ProtocolConstant.transformType == TransformType.TCP) {
				msg.resetReaderIndex();
				channel.writeAndFlush(msg);
			}else if (ProtocolConstant.transformType == TransformType.WS_BINARY){
				ByteBuf byteBuf= Unpooled.buffer(512);
				ProtocolConstant.wsBinaryEncoder.encode(channel,msg,byteBuf);
				channel.writeAndFlush(new BinaryWebSocketFrame(byteBuf));
			}else if (ProtocolConstant.transformType == TransformType.WS_TEXT){
				StringBuffer text = new StringBuffer();
				ProtocolConstant.wsTextEncoder.encode(channel,msg,text);
				channel.writeAndFlush(new TextWebSocketFrame(text.toString()));
			}else {
				msg.resetReaderIndex();
				channel.writeAndFlush(msg);
			}
		}
	}

}
