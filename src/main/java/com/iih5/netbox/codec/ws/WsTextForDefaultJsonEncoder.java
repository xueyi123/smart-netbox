package com.iih5.netbox.codec.ws;/*
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

import com.iih5.netbox.message.StringMessage;
import io.netty.channel.Channel;

public class WsTextForDefaultJsonEncoder extends WsTextEncoder{

    @Override
    public void encode(Channel ctx, Object msg, StringBuffer text) {
        synchronized (this){
            //Text协议定义: "【协议ID】#【加密类型】#【内容】",比如 10001#0#hello world!
            StringMessage jm= (StringMessage) msg;
            StringBuffer sb= new StringBuffer();
            sb.append(jm.getId());
            sb.append("#");
            sb.append(0);
            sb.append("#");
            sb.append(jm.toString());
            text.append(sb.toString());
        }
    }
}
