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
package com.iih5.example.client.tcp2;

import com.alibaba.fastjson.JSON;
import com.iih5.example.domain.UserVO;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.Charset;

public class StringOrJsonClient {
	//包字节长度,占用4个字节
	private final static int PACK_LEN = 4;
	//消息类型,占用2个字节
	private final static int TYPE_LEN = 2;
	//包头6个字节
	private final static int HEAD_SIZE =PACK_LEN+TYPE_LEN;

	static Socket socket;

	public static void main(String[] args) throws Exception {
		socket = new Socket(InetAddress.getLocalHost(), 9230);
//		for (int i = 0; i <10 ; i++) {
//			send((short) 4001,"|hello world,你好世界|",socket);
//		}
		for (int i = 0; i <10 ; i++) {
			UserVO userVO= new UserVO();
			userVO.setNickName("|hello world,你好世界|");
			userVO.setUid(1838004);
			send((short) 3001,userVO,socket);
		}
		receiv(socket);
	}

	/**
	 * 消息发送
	 * @param msgId
	 * @param content
	 * @param socket
	 * @throws Exception
     */
	public static void  send(short msgId,String content,Socket socket) throws Exception {
		byte[] buf = content.getBytes("UTF-8");
		int packLen = buf.length+ HEAD_SIZE;
		DataOutputStream dis = new DataOutputStream(socket.getOutputStream());
		//包格式：包长度(int)+消息码(short)+数据段(byte[])
		dis.writeInt(packLen);
		dis.writeShort(msgId);
		dis.write(buf);
	}
	/**
	 * 消息发送
	 * @param msgId
	 * @param json
	 * @param socket
	 * @throws Exception
	 */
	public static void  send(short msgId,Object json,Socket socket) throws Exception {
		String content= JSON.toJSONString(json);
		byte[] buf = content.getBytes("UTF-8");
		int packLen = buf.length+ HEAD_SIZE;
		DataOutputStream dis = new DataOutputStream(socket.getOutputStream());
		//包格式：包长度(int)+消息码(short)+数据段(byte[])
		dis.writeInt(packLen);
		dis.writeShort(msgId);
		dis.write(buf);
	}
	public static void  receiv(Socket socket) throws IOException {
		DataInputStream dos= new DataInputStream(socket.getInputStream());
		// 接收服务器的反馈，粘包分包方式
		while (true)
			if (dos.available()>=HEAD_SIZE) {
				int len= dos.readInt();
				short id2= dos.readShort();
				while (dos.available()<=len-HEAD_SIZE) {
				}
				byte[] b= new byte[len-HEAD_SIZE];
				dos.read(b);
				System.err.println(new String(b, Charset.forName("UTF-8")));
			}

	}
}
