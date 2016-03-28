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

/**
 * google_proto_buffer消息
 */
public class ProtoMessage extends Message{
    private byte[] message;
    private byte encrypt;
    public ProtoMessage(short id) {
        super(id);
    }
    /**
     * 获取加密类型 0=不加密，默认为0，其他用户自定义
     * @return
     */
    public byte getEncrypt() {
        return encrypt;
    }
    /**
     * 设置加密类型，0=不加密，默认为0，其他用户自定义
     * @param encrypt
     */
    public void setEncrypt(byte encrypt) {
        this.encrypt = encrypt;
    }

    /**
     * 设置消息内容
     * @param builder
     */
    public void setContent(AbstractMessageLite.Builder<?> builder){
        this.message=builder.build().toByteArray();
    }

    /**
     * 设置消息内容
     * @param content
     */
    public void setContent(byte[] content){
        this.message=content;
    }

    /**
     * 转换为具体的proto对象
     * @param builder
     * @return
     * @throws InvalidProtocolBufferException
     */
    public AbstractMessageLite.Builder<?> parseObject(AbstractMessageLite.Builder<?> builder) throws InvalidProtocolBufferException {
        return builder.mergeFrom(message);
    }

    /**
     * 返回proto内容byte[]
     * @return
     */
    public byte[] toArray() {
        return message;
    }

    @Override
    public String toString() {
        return message.toString();
    }

}
