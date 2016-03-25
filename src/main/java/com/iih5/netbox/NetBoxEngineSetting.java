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
package com.iih5.netbox;


import com.iih5.netbox.core.GlobalConstant;
import com.iih5.netbox.core.MessageType;
import com.iih5.netbox.core.ProtocolConstant;
import com.iih5.netbox.core.TransformType;
import com.iih5.netbox.message.Message;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.HttpServerCodec;

public class NetBoxEngineSetting {
	/**默认端口*/
    private int port=9230;
    /**netty Boss线程数*/
    private int bossThreadSize=1;
    /**netty Work线程数*/
    private int workerThreadSize=4;
    /**玩家管理线程数*/
    private int playerThreadSize=16;
    /**Request扫描路径*/
    private String basePackage="com";

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getBossThreadSize() {
        return bossThreadSize;
    }

    public void setBossThreadSize(int bossThreadSize) {
        this.bossThreadSize = bossThreadSize;
    }

    public int getWorkerThreadSize() {
        return workerThreadSize;
    }

    public void setWorkerThreadSize(int workerThreadSize) {
        this.workerThreadSize = workerThreadSize;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public void setMessageType(int messageType) {
        GlobalConstant.messageType =messageType;
    }

    public int getPlayerThreadSize() {
        return playerThreadSize;
    }

    public void setPlayerThreadSize(int playerThreadSize) {
        this.playerThreadSize = playerThreadSize;
    }

    public boolean isDebug() {
        return GlobalConstant.debug;
    }
    public void setDebug(boolean debug) {
        GlobalConstant.debug = debug;
    }

    /**
     * 设置编码/解码，不设置则这采用默认 ProtocolDecoder2/ProtocolEncoder2
     * @param codec 编码/解码
     */
    public void setProtocolCoder(ChannelHandler ...codec){
        ChannelHandler[] c=codec;
        if (c.length==1&& c[0] instanceof HttpServerCodec){
            GlobalConstant.transformType=TransformType.WEB_SOCKET;
        }else{
            GlobalConstant.transformType=TransformType.TCP;
            ProtocolConstant.DEFAULT_TCP_CODEC=codec;
        }

    }
}
