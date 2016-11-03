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


import com.iih5.netbox.NetBoxEngine;
import com.iih5.netbox.NetBoxEngineSetting;
import com.iih5.netbox.codec.tcp.TcpForDefaultByteDecoder;
import com.iih5.netbox.codec.tcp.TcpForDefaultByteEncoder;
import com.iih5.netbox.codec.tcp.TcpForDefaultProtoDecoder;
import com.iih5.netbox.codec.tcp.TcpForDefaultProtoEncoder;
import com.iih5.netbox.codec.ws.WsBinaryForDefaultByteDecoder;
import com.iih5.netbox.codec.ws.WsBinaryForDefaultByteEncoder;
import com.iih5.netbox.codec.ws.WsTextForDefaultJsonDecoder;
import com.iih5.netbox.codec.ws.WsTextForDefaultJsonEncoder;
import org.apache.log4j.Logger;

public class ServerStartup {
	public static void main(String[] args) throws Exception {
		System.out.println("服务端启动");
		NetBoxEngineSetting setting  = new NetBoxEngineSetting();
		setting.setBasePackage("server");//handler所在目录
		setting.setPort(9230);
		setting.setProtocolCoder(new WsTextForDefaultJsonEncoder(),new WsTextForDefaultJsonDecoder());
		setting.setDebug(true);
		NetBoxEngine boxEngine = new NetBoxEngine();
		boxEngine.setSettings(setting);
		boxEngine.start();//启动服务器
	}
}
