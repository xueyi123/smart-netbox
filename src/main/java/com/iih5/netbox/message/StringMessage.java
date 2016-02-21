package com.iih5.netbox.message;/*
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

import com.alibaba.fastjson.JSON;

import java.nio.charset.Charset;

public class StringMessage extends Message {
    private String content;
    public StringMessage(short id) {
        super(id);
    }


    public StringMessage(short id,String content) {
        super(id);
        this.content=content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public byte[] toArray() {
        return content.getBytes(Charset.forName("UTF-8"));
    }

    public void resetReaderIndex() {

    }

    public <T> T parseObject(Class<T> clazz) {
        throw new UnsupportedOperationException("StringMessage 协议不支持此方法");
    }

    public String toString(){
        return content;
    }
}
