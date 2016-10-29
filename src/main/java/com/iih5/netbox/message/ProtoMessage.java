package com.iih5.netbox.message;

import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.InvalidProtocolBufferException;
import com.iih5.netbox.codec.tcp.TcpForDefaultByteEncoder;
import com.iih5.netbox.codec.tcp.TcpForDefaultProtoDecoder;
import com.iih5.netbox.codec.tcp.TcpForDefaultProtoEncoder;
import com.iih5.netbox.codec.ws.WsBinaryForDefaultProtoEncoder;
import com.iih5.netbox.core.ProtocolConstant;
import com.iih5.netbox.core.TransformType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

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
     * 返回消息二进制
     * @return
     */
    public byte[] getMessage(){
        return  this.message;
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

    public byte[] toArray() {
        ByteBuf byteBuf=Unpooled.buffer();
        if (ProtocolConstant.transformType == TransformType.TCP){
            TcpForDefaultProtoEncoder encoder = new TcpForDefaultProtoEncoder();
            encoder.pack(this,byteBuf);
            return byteBuf.array();
        }else if (ProtocolConstant.transformType == TransformType.WS_BINARY){
            WsBinaryForDefaultProtoEncoder encoder = new WsBinaryForDefaultProtoEncoder();
            encoder.pack(this,byteBuf);
            return byteBuf.array();
        }
        return null;
    }

}
