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

import com.google.protobuf.AbstractMessage;
import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ProtoMessage extends Message{
    private byte[] message;
    public ProtoMessage(short id) {
        super(id);
    }

    public ProtoMessage(short id,byte[] message) {
        super(id);
        this.message=message;
    }
    public ProtoMessage(short id,AbstractMessageLite.Builder<?> builder) {
        super(id);
        this.message=builder.build().toByteArray();
    }
    public AbstractMessageLite.Builder<?> parseObject(AbstractMessageLite.Builder<?> builder) throws InvalidProtocolBufferException {
        return builder.mergeFrom(message);
    }
    public byte[] toArray() {
        return message;
    }

    public void resetReaderIndex() {

    }

    public <T> T parseObject(Class<T> clazz) throws Exception {
        throw new UnsupportedOperationException("ProtoMessage 协议不支持此方法");
    }

}
