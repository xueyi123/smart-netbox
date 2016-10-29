package com.iih5.netbox.message;

import com.iih5.netbox.codec.tcp.TcpForDefaultByteEncoder;
import com.iih5.netbox.codec.ws.WsBinaryForDefaultByteEncoder;
import com.iih5.netbox.codec.ws.WsBinaryForDefaultProtoEncoder;
import com.iih5.netbox.core.ProtocolConstant;
import com.iih5.netbox.core.TransformType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.Charset;

/**
 * ByteBuffer 消息对象
 */
public class ByteMessage extends Message{
    private ByteBuf buf=Unpooled.buffer();
    private byte encrypt;
    public ByteMessage(short id) {
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
     * 设置二进制内容
     * @param content
     */
    public void setContent(ByteBuf content){
        this.buf=content;
    }
    /**
     * 获取内容
     * @return
     */
    public ByteBuf getContent() {
        return buf;
    }

    /**
     * 返回内容二进制
     * @return
     */
    public byte[] getContentArray(){
        return buf.array();
    }
    /**
     * 设置二进制内容
     * @param content
     */
    public void setContent(byte[] content){
        this.buf.writeBytes(content);
    }

    /**
     * 读一个字节
     * @return
     */
    public byte readByte() {
        return buf.readByte();
    }

    /**
     * 读取bool 类型
     * @return
     */
    public boolean readBoolean() {
        return buf.readBoolean();
    }

    /**
     * 读取1个short
     * @return
     */
    public short readShort() {
        return buf.readShort();
    }

    /**
     * 读取1个int
     * @return
     */
    public int readInt() {
        return buf.readInt();
    }

    /**
     * 读取1个float
     * @return
     */
    public float readFloat() {
        return buf.readFloat();
    }

    /**
     * 读取1个long
     * @return
     */
    public long readLong() {
        return buf.readLong();
    }

    /**
     * 读取1个double
     * @return
     */
    public double readDouble() {
        return buf.readDouble();
    }

    /**
     * 读写双方要约定好，字符串定义格式(len+cotent=》2byte|nbyte),默认utf-8
     * @return
     */
    public String readString() {
        short length=buf.readShort();
        byte[] content=buf.readBytes(length).array();
        return new String(content, Charset.forName("UTF-8"));
    }
    /**
     * 读写双方要约定好，字符串定义格式(len+cotent=》2byte|nbyte),默认utf-8
     * @return
     */
    public String readString(String charset) {
        short length=buf.readShort();
        byte[] content=buf.readBytes(length).array();
        return new String(content,Charset.forName(charset));
    }

    /**
     * 写入1个布尔
     * @param b
     */
    public void writeByte(byte b) {
        buf.writeByte(b);

    }

    /**
     * 写入1布尔
     * @param b
     */
    public void writeBoolean(boolean b) {
        buf.writeBoolean(b);

    }

    /**
     * 写入1short
     * @param s
     */
    public void writeShort(int s) {
        buf.writeShort(s);

    }

    /**
     * 写入1int
     * @param i
     */
    public void writeInt(int i) {
        buf.writeInt(i);

    }

    /**
     * 写入1float
     * @param f
     */
    public void writeFloat(float f) {
        buf.writeFloat(f);

    }

    /**
     * 写入1long
     * @param l
     */
    public void writeLong(long l) {
        buf.writeLong(l);

    }

    /**
     * 写入1double
     * @param d
     */
    public void writeDouble(double d) {
        buf.writeDouble(d);

    }
    /**
     * 读写双方要约定好，字符串定义格式(len+cotent=》2byte|nbyte),默认utf-8
     * @param s
     */
    public void writeString(String s) {
        byte[] arr=s.getBytes(Charset.forName("UTF-8"));
        int length=arr.length;
        buf.writeShort(length);
        buf.writeBytes(arr);
    }
    /**
     * 读写双方要约定好，字符串定义格式(len+cotent=》2byte|nbyte),默认utf-8
     * @param s
     */
    public void writeString(String s,String charset) {
        byte[] arr=s.getBytes(Charset.forName(charset));
        int length=arr.length;
        buf.writeShort(length);
        buf.writeBytes(arr);
    }

    /**
     * 复位读指针
     */
    public void resetReaderIndex() {
        buf.resetReaderIndex();

    }

    /**
     * 复位写指针
     */
    public void resetWriterIndex() {
        buf.resetWriterIndex();
    }

    /**
     * 复位读写指针
     */
    public void resetIndex() {
        buf.resetReaderIndex();
        buf.resetWriterIndex();

    }

    public byte[] toArray() {
        ByteBuf byteBuf=Unpooled.buffer();
        if (ProtocolConstant.transformType == TransformType.TCP){
            TcpForDefaultByteEncoder encoder = new TcpForDefaultByteEncoder();
            encoder.pack(this,byteBuf);
            return byteBuf.array();
        }else if (ProtocolConstant.transformType == TransformType.WS_BINARY){
            WsBinaryForDefaultByteEncoder encoder = new WsBinaryForDefaultByteEncoder();
            encoder.pack(this,byteBuf);
            return byteBuf.array();
        }
        return null;
    }
}
