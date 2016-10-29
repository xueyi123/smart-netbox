package com.iih5.netbox.codec.ws;

import com.iih5.netbox.message.StringMessage;
import io.netty.channel.Channel;
import org.apache.log4j.Logger;

import java.util.List;

public class WsTextForDefaultJsonDecoder extends WsTextDecoder{
    private Logger logger = Logger.getLogger(WsTextForDefaultJsonDecoder.class);
    @Override
    public void decode(Channel ctx, String text, List<Object> out) {
        synchronized (this){
            //Text协议定义: "【协议ID】#【加密类型】#【内容】",比如 10001#0#hello world!
            String arr[] = text.split("#", 3);
            short msgId = Short.valueOf(arr[0]);
            byte encr =Byte.valueOf(arr[1]);
            String content = arr[2];
            StringMessage jm= new StringMessage(msgId);
            jm.setEncrypt(encr);
            jm.setContent(content);
            out.add(jm);
        }
    }
}
