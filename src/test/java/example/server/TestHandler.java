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
package example.server;


import com.google.protobuf.AbstractMessage;
import com.iih5.netbox.annotation.Protocol;
import com.iih5.netbox.annotation.Request;
import com.iih5.netbox.core.TransformType;
import com.iih5.netbox.message.ByteMessage;
import com.iih5.netbox.message.Message;
import com.iih5.netbox.message.ProtoMessage;
import com.iih5.netbox.session.ISession;
import example.client.tcp.Example;
import example.client.tcp.MyDO;

@Request
public class TestHandler {

	@Protocol(value=1001)
	public void abc2(Message msg, ISession session) throws Exception {
		Example.Message.Builder message = (Example.Message.Builder)msg.parseObject(Example.Message.newBuilder());
		System.out.println("name====="+message.getText());
		System.out.println("id="+message.getNumber());
		session.send(msg);
	}
}
