package com.iih5.example.server;/*
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

import com.iih5.example.domain.UserVO;
import com.iih5.netbox.annotation.Protocol;
import com.iih5.netbox.annotation.Request;
import com.iih5.netbox.message.JsonMessage;
import com.iih5.netbox.message.Message;
import com.iih5.netbox.session.ISession;

@Request
public class JsonMessageHandler {
    //注：协议号是不能重复的
    @Protocol(value=3001)
    public void test(Message msg, ISession session) throws Exception {
        short msgId= msg.getId();
        JsonMessage jsonMessage =(JsonMessage)msg;
        UserVO userVO= jsonMessage.parseObject(UserVO.class);
        userVO.getNickName();
        System.out.println(msg.toString());
        //TODO ...
    }
}
