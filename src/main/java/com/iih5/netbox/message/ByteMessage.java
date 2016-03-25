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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.Charset;

public class ByteMessage extends Message{

    private ByteBuf buf=Unpooled.buffer();
    public ByteMessage(short id) {
        super(id);
    }
    public ByteMessage(short id,ByteBuf buf) {
        super(id);
        this.buf=buf;
    }
    public byte readByte() {
        return buf.readByte();
    }
    public boolean readBoolean() {
        return buf.readBoolean();
    }
    public short readShort() {
        return buf.readShort();
    }
    public int readInt() {
        return buf.readInt();
    }
    public float readFloat() {
        return buf.readFloat();
    }
    public long readLong() {
        return buf.readLong();
    }
    public double readDouble() {
        return buf.readDouble();
    }

    /**
     * 读写双方要约定好，字符串定义格式(len+cotent),默认utf-8
     * @return
     */
    public String readString() {
        short length=buf.readShort();
        byte[] content=buf.readBytes(length).array();
        return new String(content, Charset.forName("UTF-8"));
    }
    /**
     * 读写双方要约定好，字符串定义格式(len+cotent),默认utf-8
     * @return
     */
    public String readString(String charset) {
        short length=buf.readShort();
        byte[] content=buf.readBytes(length).array();
        return new String(content,Charset.forName(charset));
    }

    public void writeByte(byte b) {
        buf.writeByte(b);

    }
    public void writeBoolean(boolean b) {
        buf.writeBoolean(b);

    }

    public void writeShort(int s) {
        buf.writeShort(s);

    }
    public void writeInt(int i) {
        buf.writeInt(i);

    }
    public void writeFloat(float f) {
        buf.writeFloat(f);

    }
    public void writeLong(long l) {
        buf.writeLong(l);

    }
    public void writeDouble(double d) {
        buf.writeDouble(d);

    }
    /**
     * 读写双方要约定好，字符串定义格式(len+cotent),默认utf-8
     * @param s
     */
    public void writeString(String s) {
        byte[] arr=s.getBytes(Charset.forName("UTF-8"));
        int length=arr.length;
        buf.writeShort(length);
        buf.writeBytes(arr);
    }
    /**
     * 读写双方要约定好，字符串定义格式(len+cotent),默认utf-8
     * @param s
     */
    public void writeString(String s,String charset) {
        byte[] arr=s.getBytes(Charset.forName(charset));
        int length=arr.length;
        buf.writeShort(length);
        buf.writeBytes(arr);
    }

    public ByteBuf toByteBuf() {
        return buf;
    }
    public byte[] toArray() {
        return buf.array();
    }
    public void resetReaderIndex() {
        buf.resetReaderIndex();

    }

    public <T> T parseObject(Class<T> clazz) {
        throw new UnsupportedOperationException("ByteMessage 协议不支持此方法");
    }

    public void resetWriterIndex() {
        buf.resetWriterIndex();
    }
    public void resetIndex() {
        buf.resetReaderIndex();
        buf.resetWriterIndex();

    }
}
