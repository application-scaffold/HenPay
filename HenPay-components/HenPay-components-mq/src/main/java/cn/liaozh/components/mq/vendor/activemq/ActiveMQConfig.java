package cn.liaozh.components.mq.vendor.activemq;

import cn.liaozh.components.mq.constant.MQSendTypeEnum;
import cn.liaozh.components.mq.constant.MQVenderCS;
import cn.liaozh.components.mq.model.AbstractMQ;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.Destination;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
* activeMQ的配置项
*/
@Component
@ConditionalOnProperty(name = MQVenderCS.YML_VENDER_KEY, havingValue = MQVenderCS.ACTIVE_MQ)
public class ActiveMQConfig {

    Map<String, Destination> map = new ConcurrentHashMap<>();

    public Destination getDestination(AbstractMQ mqModel){

        if(map.get(mqModel.getMQName()) == null){
            this.init(mqModel.getMQName(), mqModel.getMQType());
        }
        return map.get(mqModel.getMQName());
    }

    private synchronized void init(String mqName, MQSendTypeEnum mqSendTypeEnum){

        if(mqSendTypeEnum == MQSendTypeEnum.QUEUE){
            map.put(mqName, new ActiveMQQueue(mqName) );
        }else{
            map.put(mqName, new ActiveMQTopic(mqName) );
        }
    }

    public static final String TOPIC_LISTENER_CONTAINER = "jmsTopicListenerContainer";

    /** 新增jmsListenerContainer, 用于接收topic类型的消息 **/
    @Bean
    public JmsListenerContainerFactory<?> jmsTopicListenerContainer(ConnectionFactory factory){
        DefaultJmsListenerContainerFactory bean = new DefaultJmsListenerContainerFactory();
        bean.setPubSubDomain(true);
        bean.setConnectionFactory(factory);
        return bean;
    }

}
