
package com.iih5.netbox.session;

import com.iih5.actor.IActor;
import com.iih5.netbox.message.Message;
import io.netty.channel.Channel;

import java.util.Map;


public interface ISession {
    /**
     * 获取channel
     * @return
     */
    public Channel getChannel();

    /**
     * 获取sessionID(uuid)
     * @return
     */
    public String getId();

    /**
     * 获取actor操作入口
     * @return
     */
    public IActor getActor();

    /**
     * 发送消息
     * @param msg
     */
    public void send(Message msg);

    /**
     * 设置参数
     * @param key
     *
     * @param value
     */
    public void setParameter(String key, Object value);

    /**
     * 判断是否包含某个参数
     * @param key
     * @return
     */
    public boolean containParameter(String key);

    /**
     * 获取参数
     * @param key
     * @return
     */
    public Object getParameter(String key);

    /**
     * 设置参数
     * @param vars
     */
    public void setParameters(Map<String, Object> vars);

    /**
     * 设置信息
     * @param info
     */
    public void setInfo(Info info);

    /**
     * 获取信息
     * @return
     */
    public Info getInfo();

    /**
     * 删除参数
     * @param key
     */
    public void removeParameter(String key);

    /**
     * 清空参数
     */
    public void clearParameters();

    /**
     * 绑定用户
     * @param userId
     */
    public boolean bindUserID(String userId);
    /**
    * 解除用户绑定
    * @param userId
    */
    public void unBindUserID(String userId);
    /**
     * 获取用户自定义ID
     * @return
     */
    public String getUserID();
}
