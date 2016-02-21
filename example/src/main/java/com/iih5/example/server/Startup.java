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
package com.iih5.example.server;


import com.iih5.netbox.NetBoxEngine;
import com.iih5.netbox.NetBoxEngineSetting;
import com.iih5.netbox.core.MessageType;
import com.iih5.netbox.core.TransformType;

public class Startup {
	public static void main(String[] args) {
		System.out.println("服务端启动");
		NetBoxEngineSetting setting  = new NetBoxEngineSetting();
		setting.setBasePackage("com.iih5.example.server");//handler所在目录
		setting.setPort(9230);
		setting.setMessageType(MessageType.BYTE_TYPE);//不同的数据类型，需要要记得指定
		setting.setTransformType(TransformType.TCP);//要记得指定

		NetBoxEngine boxEngine = new NetBoxEngine();
		boxEngine.setSettings(setting);
		boxEngine.start();//启动服务器
	}
}
