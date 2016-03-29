package tcpclient;/*
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

import tcpclient.core.IHandler;
import tcpclient.core.ISession;
import tcpclient.core.Message;

public class TestHandler implements IHandler {
    public void connect(ISession session) {
        System.out.println("连接成功");
    }

    public void connectFailure(ISession session) {
        System.out.println("连接失败");
    }

    public void disConnect(ISession session) {
        System.out.println("断开连接");
    }

    public void receive(ISession session, Message message) {
        ByteMessage byteMessage = (ByteMessage)message;
        int num= byteMessage.readInt();
        double str= byteMessage.readDouble();
        String drt= byteMessage.readUTF8();
        float fl=byteMessage.readFloat();
        System.out.println("客户端：收到数据:"+ message.getId()+" "+byteMessage.readInt()+" "+num+" "+str+" "+drt+" "+fl);
    }
}
