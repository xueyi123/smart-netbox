package com.iih5.netbox.codec.ws;

import com.iih5.netbox.message.StringMessage;
import io.netty.channel.Channel;

public class WsTextForDefaultJsonEncoder extends WsTextEncoder{

    @Override
    public void encode(Channel ctx, Object msg, StringBuffer text) {
       pack(msg, text);
    }
    public void pack( Object msg, StringBuffer text){
        synchronized (this){
            //Text协议定义: "【协议ID】#【加密类型】#【内容】",比如 10001#0#hello world!
            StringMessage jm= (StringMessage) msg;
            StringBuffer sb= new StringBuffer();
            sb.append(jm.getId());
            sb.append("#");
            sb.append(0);
            sb.append("#");
            sb.append(jm.toString());
            text.append(sb.toString());
        }
    }
}
