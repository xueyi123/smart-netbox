package com.iih5.netbox.codec.ws;/*
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

import com.iih5.netbox.message.JsonMessage;
import io.netty.channel.Channel;
import org.apache.log4j.Logger;

import java.util.List;

public class WsTextForDefaultJsonDecoder extends WsTextDecoder{
    private Logger logger = Logger.getLogger(WsTextForDefaultJsonDecoder.class);
    @Override
    public void decode(Channel ctx, String text, List<Object> out) {
        synchronized (this){
            //Text协议定义: "【协议ID】#【加密类型】#【内容】",比如 10001#0#hello world!
            String arr[] = text.split("#", 3);
            short msgId = Short.valueOf(arr[0]);
            byte encr =Byte.valueOf(arr[1]);
            String content = arr[2];
            JsonMessage jm= new JsonMessage(msgId);
            jm.setEncryptType(encr);
            jm.setContent(content);
            out.add(jm);
        }
    }
}
