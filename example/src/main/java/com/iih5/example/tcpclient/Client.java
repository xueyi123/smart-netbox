package com.iih5.example.tcpclient;/*
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

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {
    /// <summary>
    /// 网络客户端
    /// 包格式：包头(byte= 1)+包长度(int= 4)+消息码(short= 2)+加密段(byte= 1)+数据段(byte[])
    ///    1）包头     :表示数据包合法性
    ///    2）包长度   :表示整个数据的长度(包含用于表示长度本身的字节)
    ///    3）消息码   :表示数据包类型
    ///    4）加密段   :表示数据段是否加密,采用什么加密算法
    ///    5）数据段   :采用byte[] 形式存放
    /// </summary>

    private boolean _status = true;
    private  Socket _socket = null;
    private  IHandler _handler;

    public Client(String ip, int port,IHandler handler) {
        _handler = handler;
        try {
            _socket = new Socket(ip,port);
            _handler.connect(new Session(this));
            ExecutorService exc=Executors.newFixedThreadPool(1);
            exc.execute(new ReceiveCallback());
        }
        catch (Exception e) {
            _handler.connectFailure(new Session(this));
        }
    }
    /// <summary>
    /// 关闭
    /// </summary>
    public void close() throws IOException {
        _socket.close();
        _status = false;
    }
    /// <summary>
    /// 发送消息
    /// </summary>
    /// <param name="message"></param>
    public void send(Message message){
        try {
            DataOutputStream  dis = new DataOutputStream(_socket.getOutputStream());
            byte[] dataBytes = message.toArray();
            int packLen = 7 + dataBytes.length;

            dis.writeByte((byte) 43);        //【header=1byte】
            dis.writeInt(packLen);           //【len   =4byte】
            dis.writeShort(message.getId()); //【msgId =2byte】
            dis.writeByte((byte) 0);        //【encr  =1byte】
            dis.write(dataBytes);           //【data  =nbyte】
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /// <summary>
    /// 接收返回数据
    /// </summary>
    /// <param name="tcpClient"></param>
    class ReceiveCallback implements Runnable {
        public void run() {
            ByteBuffer menBuffer =ByteBuffer.allocate(512);
            while (_status) {
                try {
                    DataInputStream dos= new DataInputStream(_socket.getInputStream());
                    byte header = dos.readByte();
                    if (dos.available() >= 7) {
                        int len = dos.readInt();
                        short id = dos.readShort();
                        byte encr = dos.readByte();
                        if (dos.available() < len - 7) {
                            dos.reset();
                            continue;
                        }
                        byte[] dataBuf = new byte[len - 7];
                        dos.read(dataBuf);
                        ByteMessage mes = new ByteMessage(id);
                        mes.setEncryptType(encr);
                        mes.writeBytes(dataBuf);
                        mes.flip();
                        _handler.receive(new Session(Client.this), mes);
                    }else {
                        dos.reset();
                        continue;
                    }
                }catch (Exception e){
                    _handler.disConnect(new Session(Client.this));
                    _status = false;
                }
            }
        }
    }
}
