package com.iih5.netbox.codec.ws;/*
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

import com.iih5.netbox.core.GlobalConstant;
import com.iih5.netbox.core.ProtocolConstant;
import com.iih5.netbox.message.JsonMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import org.apache.log4j.Logger;

import java.util.List;

public class WsBinaryForDefaultJsonDecoder extends WsBinaryDecoder{
    private Logger logger = Logger.getLogger(WsBinaryForDefaultJsonDecoder.class);
    @Override
    public void decode(Channel ctx, ByteBuf buffer, List<Object> out) {
        byte headFlag=buffer.readByte(); //headFlag
        if (headFlag!= ProtocolConstant.PACK_HEAD_FLAG){
            if (GlobalConstant.debug){
                logger.error("检测到包头标识错误，正确包头应为："+ProtocolConstant.PACK_HEAD_FLAG);
            }else {
                ctx.disconnect().channel().close();
            }
        }
        int packLen=buffer.readInt(); //packLen
        short msgId = buffer.readShort(); //cmdID
        byte encr=buffer.readByte(); //encr
        ByteBuf msgBuf = Unpooled.buffer(buffer.readableBytes());//content
        buffer.readBytes(msgBuf);
        JsonMessage m = new JsonMessage(msgId);
        m.setEncryptType(encr);
        m.setContent(msgBuf);
        out.add(m);
    }
}
