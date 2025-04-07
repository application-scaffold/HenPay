package cn.liaozh.components.mq.vendor;

/**
* MQ 消息接收器 接口定义
*/
public interface IMQMsgReceiver {

    /** 接收消息 **/
    void receiveMsg(String msg);
}
