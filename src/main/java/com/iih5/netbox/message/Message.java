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

import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.InvalidProtocolBufferException;

import java.lang.reflect.InvocationTargetException;

public abstract class Message {
    /**消息id*/
    private short id;
    /**加密类型，默认不加密*/
    private byte encryptType=0;
    /**
     * @param id 协议ID
     */
    public Message(short id){
        this.id=id;
    }
    /**
     * @param id 协议ID
     * @param encr 加密类型，默认不加密
     */
    public Message(short id,byte encr){
        this.id=id;
        this.encryptType=encr;
    }
    public short getId() {
        return id;
    }
    public void setId(short id) {
        this.id = id;
    }

    public byte getEncryptType() {
        return encryptType;
    }
    public void setEncryptType(byte encryptType) {
        this.encryptType = encryptType;
    }
    public abstract  byte[] toArray();

    public abstract void resetReaderIndex();

    public abstract <T> T parseObject(Class<T> clazz) throws  Exception;

    public AbstractMessageLite.Builder<?> parseObject(AbstractMessageLite.Builder<?> builder) throws InvalidProtocolBufferException {

        return null;
    }
}
