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
package server;


import com.iih5.netbox.annotation.Protocol;
import com.iih5.netbox.annotation.Request;
import com.iih5.netbox.message.Message;
import com.iih5.netbox.message.ProtoMessage;
import com.iih5.netbox.session.ISession;
import proto.Example;

import java.util.concurrent.TimeUnit;

@Request
public class ProtoMessageHandler {
	//注：协议号是不能重复的
	@Protocol(value=1001)
	public void abc2(Message msg, ISession session) throws Exception {
		ProtoMessage protoMessage = (ProtoMessage)msg;
		Example.Message.Builder message = (Example.Message.Builder)protoMessage.parseObject(Example.Message.newBuilder());
		message.getText();
		message.getNumber();
		System.out.println(message.getText());
		session.send(msg);
		//waitForNotice(msg,session);
		//TODO ...
	}
	//定时通知
	public void  waitForNotice(Message message,ISession session){
		session.getActor().scheduledTask(new Runnable() {
			public void run() {
				System.out.println("hello world");
			}
		},1, TimeUnit.SECONDS);
	}
}
