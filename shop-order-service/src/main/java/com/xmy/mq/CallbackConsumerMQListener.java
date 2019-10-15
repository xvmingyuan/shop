package com.xmy.mq;

import com.alibaba.fastjson.JSON;
import com.xmy.constant.ShopCode;
import com.xmy.entity.OrderResult;
import com.xmy.entity.Result;
import com.xmy.mapper.ShopOrderMapper;
import com.xmy.mapper.ShopOrderMqStatusLogMapper;
import com.xmy.pojo.ShopOrder;
import com.xmy.pojo.ShopOrderMqStatusLog;
import com.xmy.pojo.ShopOrderMqStatusLogExample;
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
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;


@SuppressWarnings("ALL")
@Slf4j
@SpringBootConfiguration
public class CallbackConsumerMQListener {

    @Autowired
    private ShopOrderMapper orderMapper;

    @Autowired
    private ShopOrderMqStatusLogMapper orderMqStatusLogMapper;

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
        private ConcurrentSkipListMap<Long, Integer> localTransTrue = new ConcurrentSkipListMap<Long, Integer>();
        private ConcurrentSkipListMap<Long, Integer> localTransFalse = new ConcurrentSkipListMap<Long, Integer>();

        @Override
        public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
            for (MessageExt messageExt : msgs) {
                try {
                    // 1 解析消息内容
                    String body = new String(messageExt.getBody(), "UTF-8");
                    log.info("订单确认服务,接受到消息");
                    Result result = JSON.parseObject(body, Result.class);
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
                    if (result.getSuccess() == ShopCode.SHOP_SUCCESS.getSuccess()) {
                        integer = localTransTrue.get(orderId);
                        transactionIndex = new AtomicInteger(0);
                        if (integer != null) {
                            transactionIndex = new AtomicInteger(integer);
                        }
                        value = transactionIndex.incrementAndGet();
                        localTransTrue.put(orderId, value);
                        updateOrderMQStateLog(orderResult);
                    } else if (result.getSuccess() == ShopCode.SHOP_FAIL.getSuccess()) {
                        integer = localTransFalse.get(orderId);
                        transactionIndex = new AtomicInteger(0);
                        if (integer != null) {
                            transactionIndex = new AtomicInteger(integer);
                        }
                        value = transactionIndex.incrementAndGet();
                        localTransFalse.put(orderId, value);
                        updateOrderMQStateLog(orderResult);
                    }

                    if (localTrans.get(orderId) != null && localTrans.get(orderId) >= 3) {
                        // 2 查询订单
                        ShopOrder order = orderMapper.selectByPrimaryKey(orderId);
                        if (localTransTrue.get(orderId) != null && localTransTrue.get(orderId) >= 3) {
                            //3  更新订单状态为已确认
                            order.setOrderStatus(ShopCode.SHOP_ORDER_CONFIRM.getCode());
                            order.setPayStatus(ShopCode.SHOP_ORDER_PAY_STATUS_NO_PAY.getCode());
                            order.setConfirmTime(new Date());
                            orderMapper.updateByPrimaryKey(order);
                            localTransTrue.remove(orderId);
                            log.info("订单状态设置为已确认");
                        }
                        if (localTransFalse.get(orderId) != null && localTransFalse.get(orderId) > 0) {
                            //4  更新订单状态为下单失败
                            order.setOrderStatus(ShopCode.SHOP_ORDER_CALL_ERROR.getCode());
                            orderMapper.updateByPrimaryKey(order);
                            localTransFalse.remove(orderId);
                            log.info("订单状态设置为已确认失败");

                        }
                        localTrans.remove(orderId);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log.info("确认失败");
                }
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }

        private void updateOrderMQStateLog(OrderResult orderResult) {
            ShopOrderMqStatusLog orderMqStatusLog = orderMqStatusLogMapper.selectByPrimaryKey(orderResult.getOrderId());
            ShopOrderMqStatusLogExample mqStatusLogExample = new ShopOrderMqStatusLogExample();
            ShopOrderMqStatusLogExample.Criteria criteria = mqStatusLogExample.createCriteria();
            criteria.andOrderIdEqualTo(orderResult.getOrderId());
            criteria.andCouponStatusEqualTo(orderMqStatusLog.getCouponStatus());
            criteria.andGoodsStatusEqualTo(orderMqStatusLog.getGoodsStatus());
            criteria.andUserMoneyStatusEqualTo(orderMqStatusLog.getUserMoneyStatus());
            switch (orderResult.getSourceCode()) {
                case "coupon":
                    orderMqStatusLog.setCouponStatus(orderResult.getStatus() ? 1 : 0);
                    orderMqStatusLog.setCouponResult(orderResult.getMessage());
                    break;
                case "goods":
                    orderMqStatusLog.setGoodsStatus(orderResult.getStatus() ? 1 : 0);
                    orderMqStatusLog.setGoodsResult(orderResult.getMessage());
                    break;
                case "usermoney":
                    orderMqStatusLog.setUserMoneyStatus(orderResult.getStatus() ? 1 : 0);
                    orderMqStatusLog.setUserResult(orderResult.getMessage());
                    break;
            }
            int r = orderMqStatusLogMapper.updateByExample(orderMqStatusLog, mqStatusLogExample);
            while (r <= 0) {
                log.info("并发修改订单状态日志");
                orderMqStatusLog = orderMqStatusLogMapper.selectByPrimaryKey(orderResult.getOrderId());
                mqStatusLogExample = new ShopOrderMqStatusLogExample();
                criteria = mqStatusLogExample.createCriteria();
                criteria.andOrderIdEqualTo(orderResult.getOrderId());
                criteria.andCouponStatusEqualTo(orderMqStatusLog.getCouponStatus());
                criteria.andGoodsStatusEqualTo(orderMqStatusLog.getGoodsStatus());
                criteria.andUserMoneyStatusEqualTo(orderMqStatusLog.getUserMoneyStatus());
                switch (orderResult.getSourceCode()) {
                    case "coupon":
                        orderMqStatusLog.setCouponStatus(orderResult.getStatus() ? 1 : 0);
                        orderMqStatusLog.setCouponResult(orderResult.getMessage());
                        break;
                    case "goods":
                        orderMqStatusLog.setGoodsStatus(orderResult.getStatus() ? 1 : 0);
                        orderMqStatusLog.setGoodsResult(orderResult.getMessage());
                        break;
                    case "usermoney":
                        orderMqStatusLog.setUserMoneyStatus(orderResult.getStatus() ? 1 : 0);
                        orderMqStatusLog.setUserResult(orderResult.getMessage());
                        break;
                }
                r = orderMqStatusLogMapper.updateByExample(orderMqStatusLog, mqStatusLogExample);
            }

        }


    }
}
