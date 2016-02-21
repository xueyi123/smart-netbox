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
package example.client.tcp2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.Charset;

public class MyClient {
	static Socket socket;

	public static void main(String[] args) throws Exception {
		socket = new Socket(InetAddress.getLocalHost(), 9230);
		//包格式：包长度(int)+消息码(short)+加密(byte)+数据段(byte[])
		String buff= "hello world,你好世界";
		byte[] buf = buff.getBytes("UTF-8");
		int packLen = buf.length+ 2+4+1;
		short id=10001;
		for (int i = 0; i < 1; i++) {
			DataOutputStream dis = new DataOutputStream(socket.getOutputStream());
			dis.writeInt(packLen);
			dis.writeShort(id);
			dis.writeByte(0);
			dis.write(buf);

		}
		DataInputStream dos= new DataInputStream(socket.getInputStream());
		// 接收服务器的反馈，粘包分包方式
		while (true)
			if (dos.available()>=7) {
				int len= dos.readInt();
				short id2= dos.readShort();
				byte encr=dos.readByte();
				while (dos.available()<len-7) {	
				}
				byte[] b= new byte[len-6];
				dos.read(b);
				System.err.println(len);
				System.err.println(id2);
				System.err.println(new String(b, Charset.forName("UTF-8")));
			}

	}
}
