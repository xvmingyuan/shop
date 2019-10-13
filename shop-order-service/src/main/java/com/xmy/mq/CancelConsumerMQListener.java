package com.xmy.mq;

import com.alibaba.fastjson.JSON;
import com.xmy.constant.ShopCode;
import com.xmy.entity.MQEntity;
import com.xmy.mapper.ShopOrderMapper;
import com.xmy.pojo.ShopOrder;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SuppressWarnings("ALL")
@Slf4j
@SpringBootConfiguration
public class CancelConsumerMQListener {

    @Autowired
    private ShopOrderMapper orderMapper;

    @Value("${mq.rocketmq.name-server}")
    private String namesrvAddr;
    @Value("${mq.order.consumer.group.name}")
    private String groupName;
    @Value("${mq.order.topic}")
    private String topic;
    @Value("${mq.rocketmq.consumer.tag}")
    private String tag;
    @Value("${mq.rocketmq.consumer.consumeThreadMin}")
    private int consumeThreadMin;
    @Value("${mq.rocketmq.consumer.consumeThreadMax}")
    private int consumeThreadMax;


    @Bean
    public DefaultMQPushConsumer getCancelRocketMQConsumer() throws MQClientException {

        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(groupName);
        consumer.setNamesrvAddr(namesrvAddr);
        consumer.setConsumeThreadMin(consumeThreadMin);
        consumer.setConsumeThreadMax(consumeThreadMax);
        consumer.setMessageModel(MessageModel.BROADCASTING);
        consumer.registerMessageListener(new CancelConsumerMessageListener());
        consumer.subscribe(topic, this.tag);
        consumer.start();
        System.out.println("CancelConsumerMQListener消费者启动");
        return consumer;
    }

    class CancelConsumerMessageListener implements MessageListenerConcurrently {
        @Override
        public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
            for (MessageExt messageExt : msgs) {
                try {
                    // 1 解析消息内容
                    String body = new String(messageExt.getBody(), "UTF-8");
                    MQEntity mqEntity = JSON.parseObject(body, MQEntity.class);
                    log.info("订单取消服务,接受到消息");
                    if (mqEntity.getOrderId() != null) {
                        // 2 查询订单
                        ShopOrder order = orderMapper.selectByPrimaryKey(mqEntity.getOrderId());
                        //3  更新订单状态为取消
                        order.setOrderStatus(ShopCode.SHOP_ORDER_CANCEL.getCode());
                        orderMapper.updateByPrimaryKey(order);
                        log.info("订单状态设置为取消");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log.info("订单取消失败");
                }
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
    }
}
