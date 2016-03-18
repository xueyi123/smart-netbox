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
package com.iih5.example.client.tcp.jsonclient;


import com.iih5.netbox.core.GlobalConstant;
import com.iih5.netbox.core.MessageType;
import com.iih5.netbox.core.ProtocolConstant;
import com.iih5.netbox.core.TcpCodecType;

public class Startup {
	public static JsonClient client;
	public static void main(String[] args) {
		System.err.println("---------------TCP测试---------------");
		client = new JsonClient();
		client.setMessageType(MessageType.JSON_TYPE);
		GlobalConstant.debug=true;
		client.connect("127.0.0.1", 9230);
	}
}
