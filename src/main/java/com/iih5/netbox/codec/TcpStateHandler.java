/*
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
package com.iih5.netbox.codec;


import com.iih5.netbox.NetBoxEngine;
import com.iih5.netbox.actor.IActor;
import com.iih5.netbox.core.AnnObject;
import com.iih5.netbox.core.CmdHandlerCache;
import com.iih5.netbox.core.GlobalConstant;
import com.iih5.netbox.message.Message;
import com.iih5.netbox.session.ISession;
import com.iih5.netbox.session.Session;
import com.iih5.netbox.session.SessionManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.log4j.Logger;


public class TcpStateHandler extends ChannelInboundHandlerAdapter {
    private static Logger logger =Logger.getLogger(WebSocketServerHandler.class);
    //连接成功
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (GlobalConstant.debug){
            logger.info("连接成功");
        }
        //连接成功,绑定工作线程
        IActor actor = SessionManager.getInstance().createActor();
        Session session = new Session(ctx.channel(), actor);
        SessionManager.getInstance().addSession(ctx.channel(), session);
        if (NetBoxEngine.extension != null) {
            NetBoxEngine.extension.connect(session);
        }
    }

    //断开连接
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (GlobalConstant.debug){
            logger.info("断开连接");
        }
        //断开连接,解除工作线程
        ISession session = SessionManager.getInstance().getSession(ctx.channel());
        if (NetBoxEngine.extension != null) {
            NetBoxEngine.extension.disConnect(session);
        }
        SessionManager.getInstance().removeSession(ctx.channel());
    }
    //读取数据
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (GlobalConstant.debug){
            logger.info("读取数据...");
        }
        if (msg != null && msg instanceof Message) {
            final Message message = (Message) msg;
            final Channel channel = ctx.channel();
            final AnnObject cmdHandler = CmdHandlerCache.getInstance().getAnnObject(message.getId());
            final ISession session = SessionManager.getInstance().getSession(channel);
            if (cmdHandler != null && session != null) {
                session.getActor().execute(new Runnable() {
                    public void run() {
                        try {
                            cmdHandler.getMethod().invoke(cmdHandler.getClas().newInstance(), message, session);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                logger.error("cmdId:" + message.getId() + " 不存在");
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
      //TODO
    }

    //空闲处理，读、写、读或写3种类型
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        logger.error("闲置（写、读）时间过长，服务器主动关闭链接" + ctx.channel().remoteAddress() + " 的连接 ");
        ctx.close();
        super.userEventTriggered(ctx, evt);
    }

    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }
}
