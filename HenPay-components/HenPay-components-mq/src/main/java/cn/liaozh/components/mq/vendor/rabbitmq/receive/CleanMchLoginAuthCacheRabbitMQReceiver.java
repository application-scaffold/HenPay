package cn.liaozh.components.mq.vendor.rabbitmq.receive;

import cn.liaozh.components.mq.constant.MQVenderCS;
import cn.liaozh.components.mq.executor.MqThreadExecutor;
import cn.liaozh.components.mq.model.CleanMchLoginAuthCacheMQ;
import cn.liaozh.components.mq.vendor.IMQMsgReceiver;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * rabbitMQ消息接收器：仅在vender=rabbitMQ时 && 项目实现IMQReceiver接口时 进行实例化
 * 业务：  清除商户登录信息
 */
@Component
@ConditionalOnProperty(name = MQVenderCS.YML_VENDER_KEY, havingValue = MQVenderCS.RABBIT_MQ)
@ConditionalOnBean(CleanMchLoginAuthCacheMQ.IMQReceiver.class)
public class CleanMchLoginAuthCacheRabbitMQReceiver implements IMQMsgReceiver {

    @Autowired
    private CleanMchLoginAuthCacheMQ.IMQReceiver mqReceiver;

    /** 接收 【 queue 】 类型的消息 **/
    @Override
    @Async(MqThreadExecutor.EXECUTOR_PAYORDER_MCH_NOTIFY)
    @RabbitListener(queues = CleanMchLoginAuthCacheMQ.MQ_NAME)
    public void receiveMsg(String msg){
        mqReceiver.receive(CleanMchLoginAuthCacheMQ.parse(msg));
    }

}
