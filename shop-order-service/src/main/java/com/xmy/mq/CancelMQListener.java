package com.xmy.mq;

import com.alibaba.fastjson.JSON;
import com.xmy.constant.ShopCode;
import com.xmy.entity.MQEntity;
import com.xmy.mapper.ShopOrderMapper;
import com.xmy.pojo.ShopOrder;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@SuppressWarnings("ALL")
@Slf4j
@Component
@RocketMQMessageListener(topic = "${mq.order.topic}",
        consumerGroup = "${mq.order.consumer.group.name}",
        messageModel = MessageModel.BROADCASTING)
public class CancelMQListener implements RocketMQListener<MessageExt> {
    @Autowired
    private ShopOrderMapper orderMapper;

    @Override
    public void onMessage(MessageExt messageExt) {
        try {
            // 1 解析消息内容
            String body = new String(messageExt.getBody(), "UTF-8");
            MQEntity mqEntity = JSON.parseObject(body, MQEntity.class);
            log.info("订单服务,接受到消息");
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
}
