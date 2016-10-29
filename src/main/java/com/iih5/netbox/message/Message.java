package com.iih5.netbox.message;

/**
 * 消息基础类，是一个抽象类
 */
public abstract class Message {
    /**消息id*/
    private short id;
    public Message(short msgId){
        this.id=msgId;
    }
    public short getId() {
        return id;
    }
    public void setId(short id) {
        this.id = id;
    }
    public abstract  byte[] toArray();
}
