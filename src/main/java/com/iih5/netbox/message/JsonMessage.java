package com.iih5.netbox.message;/*
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

import com.alibaba.fastjson.JSON;

import java.nio.charset.Charset;
import java.util.List;

public class JsonMessage extends Message {
    private String content;
    public JsonMessage(short id) {
        super(id);
    }

    public void setContent(String content){
        this.content=content;
    }
    public void setContent(Object json){
        this.content=JSON.toJSONString(json);
    }
    public <T> T parseObject(Class<T> clazz) {
        return  JSON.parseObject(content,clazz);
    }

    public <T> List<T> parseArray(Class<T> clazz) {
       return JSON.parseArray(content,clazz);
    }

    public String toString(){
        return  content;
    }

    public byte[] toArray() {
        return content.getBytes(Charset.forName("UTF-8"));
    }


    public void resetReaderIndex() {

    }

}
