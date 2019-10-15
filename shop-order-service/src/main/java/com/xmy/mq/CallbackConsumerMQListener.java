package com.xmy.mq;

import com.alibaba.fastjson.JSON;
import com.xmy.constant.ShopCode;
import com.xmy.entity.OrderResult;
import com.xmy.entity.Result;
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

import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("ALL")
@Slf4j
@SpringBootConfiguration
public class CallbackConsumerMQListener {

    @Autowired
    private ShopOrderMapper orderMapper;

    @Value("${mq.rocketmq.name-server}")
    private String namesrvAddr;

    @Value("${mq.order.confirm.consumer.callback.group}")
    private String groupName;

    @Value("${mq.order.confirm.consumer.callback.topic}")
    private String callbackTopic;

    @Value("${mq.order.confirm.consumer.callback.tag}")
    private String callbackTag;

    @Value("${mq.rocketmq.consumer.consumeThreadMin}")
    private int consumeThreadMin;

    @Value("${mq.rocketmq.consumer.consumeThreadMax}")
    private int consumeThreadMax;


    @Bean
    public DefaultMQPushConsumer getCallbackRocketMQConsumer() throws MQClientException {

        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(groupName);
        consumer.setNamesrvAddr(namesrvAddr);
        consumer.setConsumeThreadMin(consumeThreadMin);
        consumer.setConsumeThreadMax(consumeThreadMax);
        consumer.setMessageModel(MessageModel.CLUSTERING);
        consumer.registerMessageListener(new CallbackConsumerMessageListener());
        consumer.subscribe(callbackTopic, callbackTag);
        consumer.start();
        System.out.println("CallbackConsumerMQListener消费者启动");
        return consumer;
    }

    class CallbackConsumerMessageListener implements MessageListenerConcurrently {

        private ConcurrentSkipListMap<Long, Integer> localTrans = new ConcurrentSkipListMap<Long, Integer>();

        @Override
        public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
            for (MessageExt messageExt : msgs) {
                try {
                    // 1 解析消息内容
                    String body = new String(messageExt.getBody(), "UTF-8");
                    Result result = JSON.parseObject(body, Result.class);
                    log.info("订单确认服务,接受到消息");
                    if (result.getSuccess() == ShopCode.SHOP_SUCCESS.getSuccess()) {
                        String message = result.getMessage();
                        OrderResult orderResult = JSON.parseObject(message, OrderResult.class);
                        Long orderId = orderResult.getOrderId();
                        Integer integer = localTrans.get(orderId);
                        AtomicInteger transactionIndex = new AtomicInteger(0);
                        if (integer != null) {
                            transactionIndex = new AtomicInteger(integer);
                        }
                        int value = transactionIndex.incrementAndGet();
                        localTrans.put(orderId, value);
                        if (localTrans.get(orderId) >= 3) {
                            // 2 查询订单
                            ShopOrder order = orderMapper.selectByPrimaryKey(orderId);
                            //3  更新订单状态为取消
                            order.setOrderStatus(ShopCode.SHOP_ORDER_CONFIRM.getCode());
                            order.setPayStatus(ShopCode.SHOP_ORDER_PAY_STATUS_NO_PAY.getCode());
                            order.setConfirmTime(new Date());
                            orderMapper.updateByPrimaryKey(order);
                            log.info("订单状态设置为已确认");
                            localTrans.remove(orderId);
                        }
                    } else {
                        log.info("订单状态设置为已确认失败");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log.info("订单状态设置为已确认失败");
                }
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
    }
}
