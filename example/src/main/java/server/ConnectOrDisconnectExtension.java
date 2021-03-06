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


import com.iih5.netbox.annotation.InOut;
import com.iih5.netbox.core.ConnectExtension;
import com.iih5.netbox.session.ISession;
import com.iih5.netbox.session.SessionManager;

@InOut("connect/disconnect callback")
public class ConnectOrDisconnectExtension extends ConnectExtension {

	public void connect(ISession session) {
		// TODO Auto-generated method stub
		System.err.println("n,,,,,,,,连接成功、、、、、、、、、、、...");

	}

	public void disConnect(ISession session) {
		// TODO Auto-generated method stub
		System.err.println(",,,,,,,,,断开连接、、、、、、、、、、、...");
	}


}
