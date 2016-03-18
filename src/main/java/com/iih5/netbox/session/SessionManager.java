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

import com.iih5.netbox.actor.CurrentUtils;
import com.iih5.netbox.actor.IActor;
import com.iih5.netbox.actor.QueueActorManager;
import io.netty.channel.Channel;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
	//channel<->session的关联
	private Map<Channel, ISession> channelSession = new ConcurrentHashMap<Channel, ISession>();
	//uid<->channel的关联
	private Map<String, Channel> uidChannel = new ConcurrentHashMap<String, Channel>();
	//actor管理
	private QueueActorManager actorManager;
	//actor管理线程数量
	private int threadNum=16;
	private static SessionManager cache=new SessionManager();
	public static synchronized SessionManager getInstance(){
		if (cache==null) {
			cache= new SessionManager();
		}
		return cache;
	}

	/**
	 * 绑定用户关联
	 * @param userId
	 * @param channel
     * @return true 绑定成功
     */
	public boolean bindUserIDAndChannel(String userId,Channel channel){
		if (channelSession.get(channel)!=null){
			uidChannel.put(userId,channel);
			return true;
		}else {
			return false;
		}
	}
	/**
	 * 解除绑定用户关联
	 * @param userId
	 */
	public void unBindUserIDAndChannel(String userId){
		uidChannel.remove(userId);
	}
	/**
	 * 设置session线程管理数量，默认16个
	 * @param threadNum
     */
	public void setSessionTheadNum(int threadNum){
		this.threadNum=threadNum;
	}
	public void setActorManager(QueueActorManager manager) {
		this.actorManager=manager;
	}
	public IActor createActor() {
		if (this.actorManager==null) {
			this.actorManager = new QueueActorManager(threadNum, CurrentUtils.createThreadFactory("User-Pool-"));
		}
		return actorManager.createActor();
	}
	/**
	 * 添加sesion
	 * @param channel
	 * @param session
     */
	public void addSession(Channel channel,Session session) {
		channelSession.put(channel, session);
	}
	/**
	 * 删除session
	 * @param channel
     */
	public void removeSession(Channel channel) {
		ISession session =channelSession.get(channel);
		if (session!=null){
			String uid= session.getUserID();
			if (uid!=null){
				uidChannel.remove(uid);
			}
		}
		channelSession.remove(channel);
	}
	/**
	 * 获取session
	 * @param channel
	 * @return
     */
	public ISession getSession(Channel channel) {
		if (channelSession.containsKey(channel)) {
			ISession session = channelSession.get(channel);
			if (session==null) {
				return null;
			}
			return session;
		}
		return null;
	}
	/**
	 * 获取session
	 * @param userId
	 * @return
	 */
	public ISession getSession(String userId){
		if (uidChannel.containsKey(userId)) {
			Channel channel = uidChannel.get(userId);
			if (channel!=null){
				return  channelSession.get(channel);
			}
		}
		return null;
	}

	/**
	 * 获取session列表
	 * @return
     */
	public Collection<ISession> getAllSessions(){
		return channelSession.values();
	}
}
