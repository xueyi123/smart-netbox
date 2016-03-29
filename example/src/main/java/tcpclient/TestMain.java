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

import tcpclient.core.Client;

public class TestMain {
    public static void main(String[] args) {
        Client client = new Client("127.0.0.1",9230,new TestHandler());
        ByteMessage message = new ByteMessage((short) 2001);
        message.writeInt(225);
        message.writeDouble(1000.1);
        message.writeUTF8("king,你好,welcome to china");
        message.writeFloat(1000F);
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        for (int i = 0; i < 100; i++) {
            client.send(message);
        }


    }
}
