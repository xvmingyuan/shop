package com.xmy.config;

import com.xmy.listener.OrderTransactionMQListenerImpl;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootConfiguration
public class MQProducerConfiguration {
    @Value("${mq.rocketmq.producer.group}")
    private String producerGroup;

    @Value("${mq.rocketmq.producer.transaction.group}")
    private String producerTransactionGroup;

    @Value("${mq.rocketmq.name-server}")
    private String namesrvAddr;

    @Value("${mq.rocketmq.producer.send-message-timeout}")
    private int sendMsgTimeout;

    @Bean()
    TransactionMQProducer producer() throws MQClientException {
        // 1 创建消息生产者 producer 并制定生产者组名
        TransactionMQProducer producer = new TransactionMQProducer(producerTransactionGroup);
        // 2 指定NameServer地址
        producer.setNamesrvAddr(namesrvAddr);
        // 设置回调
        producer.setTransactionListener(new OrderTransactionMQListenerImpl());
        // 超时时间设置
        producer.setSendMsgTimeout(sendMsgTimeout);
        // 3 启动producer
        producer.start();
        System.out.println("事务生产者启动");
        return producer;
    }

    @Bean
    public DefaultMQProducer getRocketMQProducer() throws MQClientException {
        DefaultMQProducer producer;
        producer = new DefaultMQProducer(producerGroup);
        producer.setNamesrvAddr(namesrvAddr);
        producer.setSendMsgTimeout(sendMsgTimeout);
        producer.start();
        System.out.println("默认生产者启动");
        return producer;
    }
}
