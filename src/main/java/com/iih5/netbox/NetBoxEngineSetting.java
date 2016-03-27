/*
 * Copyright 2016 xueyi (1581249005@qq.com)
 *
 * The Smart-NetBox Project licenses this file to you under the Apache License,
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

import com.iih5.netbox.codec.tcp.TcpDecoder;
import com.iih5.netbox.codec.tcp.TcpEncoder;
import com.iih5.netbox.codec.ws.WsBinaryDecoder;
import com.iih5.netbox.codec.ws.WsBinaryEncoder;
import com.iih5.netbox.codec.ws.WsTextDecoder;
import com.iih5.netbox.codec.ws.WsTextEncoder;
import com.iih5.netbox.core.GlobalConstant;
import com.iih5.netbox.core.ProtocolConstant;
import com.iih5.netbox.core.TransformType;

import javax.naming.OperationNotSupportedException;

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

    /**
     * 设置端口
     * @param port
     */
    public void setPort(int port) {
        this.port = port;
    }

    public int getBossThreadSize() {
        return bossThreadSize;
    }

    /**
     * 设置监听线程数量
     * @param bossThreadSize
     */
    public void setBossThreadSize(int bossThreadSize) {
        this.bossThreadSize = bossThreadSize;
    }

    public int getWorkerThreadSize() {
        return workerThreadSize;
    }

    /**
     * 设置框架工作线程数量
     * @param workerThreadSize
     */
    public void setWorkerThreadSize(int workerThreadSize) {
        this.workerThreadSize = workerThreadSize;
    }

    public String getBasePackage() {
        return basePackage;
    }

    /**
     * 设置注解扫描路径
     * @param basePackage
     */
    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public int getPlayerThreadSize() {
        return playerThreadSize;
    }

    /**
     *设置用户操作线程数
     * @param playerThreadSize
     */
    public void setPlayerThreadSize(int playerThreadSize) {
        this.playerThreadSize = playerThreadSize;
    }

    public boolean isDebug() {
        return GlobalConstant.debug;
    }

    /**
     * 设置调试模式，true为调试模式
     * @param debug
     */
    public void setDebug(boolean debug) {
        GlobalConstant.debug = debug;
    }

    /**
     * 设置编码/解码，不设置则这采用默认TCP的 ProtocolDecoder2/ProtocolEncoder2
     * @param encode 编码
     */
    public void setProtocolCoder(Object encode,Object decode) throws Exception {
        if (encode instanceof TcpEncoder && decode instanceof TcpDecoder){
            ProtocolConstant.transformType=TransformType.TCP;
            ProtocolConstant.tcpEncoder=(TcpEncoder) encode;
            ProtocolConstant.tcpDecoder=(TcpDecoder) decode;
        }else if(encode instanceof WsTextEncoder && decode instanceof WsTextDecoder){
            ProtocolConstant.transformType=TransformType.WS_TEXT;
            ProtocolConstant.wsTextEncoder=(WsTextEncoder)encode;
            ProtocolConstant.wsTextDecoder=(WsTextDecoder)decode;
        }else if (encode instanceof WsBinaryEncoder && decode instanceof WsBinaryDecoder){
            ProtocolConstant.transformType=TransformType.WS_BINARY;
            ProtocolConstant.wsBinaryEncoder=(WsBinaryEncoder)encode;
            ProtocolConstant.wsBinaryDecoder=(WsBinaryDecoder)decode;
        }else {
           throw new OperationNotSupportedException("设置编码/解码错误");
        }
    }

}
