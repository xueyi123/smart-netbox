package com.iih5.example.tcpclient;/*
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

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public class ByteMessage extends Message {
    private ByteBuffer buffer = ByteBuffer.allocate(512);
    public ByteMessage(short id) {
        super(id);
    }
    public byte readByte() {
        return  buffer.get();
    }
    public void readBytes(byte[] dst, int offset, int len){
        buffer.get(dst, offset, len);
    }
    public short readShort() {
        return buffer.getShort();
    }
    public int readInt() {
        return buffer.getInt();
    }
    public float readFloat() {
        return buffer.getFloat();
    }
    public long readLong() {
        return buffer.getLong();
    }
    public double readDouble() {
        return buffer.getDouble();
    }

    /**
     * 读写双方要约定好，字符串定义格式(len+cotent),默认utf-8
     * @return
     */
    public String readUTF8() {
        try {
            short length = buffer.getShort();
            byte[] content = new byte[length];
            buffer.get(content,0,length);
            return new String(content,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void writeByte(byte b) {
        buffer.put(b);
    }

    public void writeBytes(byte[] buf)
    {
        buffer.put(buf);
    }
    public void writeShort(short s)
    {
        buffer.putShort(s);
    }
    public void writeInt(int i)
    {
        buffer.putInt(i);

    }
    public void writeFloat(float f)
    {
        buffer.putFloat(f);
    }
    public void writeLong(long l)
    {
        buffer.putLong(l);

    }
    public void writeDouble(double d)
    {
        buffer.putDouble(d);

    }

    /**
     * 读写双方要约定好，字符串定义格式(len+cotent),默认utf-8
     * @param s
     * @throws UnsupportedEncodingException
     */
    public void writeUTF8(String s) {
        try{
            byte[] arr = s.getBytes("UTF-8");
            short length = (short)arr.length;
            buffer.putShort(length);
            buffer.put(arr);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void resetReaderIndex()
    {
        //buffer.resetReaderIndex();
        buffer.reset();
    }

    public byte[] toArray()
    {
        return buffer.array();
    }
    public void resetWriterIndex()
    {
        //buffer.resetWriterIndex();
        buffer.reset();
    }
    public void resetIndex()
    {
        buffer.reset();
    }

    public void flip(){
        buffer.flip();
    }
}
