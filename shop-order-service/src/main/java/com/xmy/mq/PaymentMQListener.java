package com.xmy.mq;

import com.alibaba.fastjson.JSON;
import com.xmy.constant.ShopCode;
import com.xmy.exception.CastException;
import com.xmy.mapper.ShopOrderMapper;
import com.xmy.pojo.ShopOrder;
import com.xmy.pojo.ShopPay;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
@SuppressWarnings("ALL")
@Slf4j
@Component
@RocketMQMessageListener(topic = "${mq.pay.topic}",
        consumerGroup = "${mq.pay.consumer.group.name}",
        messageModel = MessageModel.BROADCASTING)
public class PaymentMQListener implements RocketMQListener<MessageExt> {

    @Autowired
    private ShopOrderMapper orderMapper;

    @Override
    public void onMessage(MessageExt messageExt) {
        try {

            // 1 解析消息
            String body = new String(messageExt.getBody(), "UTF-8");
            ShopPay pay = JSON.parseObject(body, ShopPay.class);
            log.info("订单服务,支付MQ调用");
            if (pay != null &&
                    pay.getOrderId() != null &&
                    pay.getPayId() != null &&
                    pay.getIsPaid().intValue() == ShopCode.SHOP_ORDER_PAY_STATUS_IS_PAY.getCode().intValue()) {
                // 2 查询订单
                ShopOrder order = orderMapper.selectByPrimaryKey(pay.getOrderId());
                // 3 更改订单状态
                order.setPayStatus(ShopCode.SHOP_ORDER_PAY_STATUS_IS_PAY.getCode());
                order.setPayTime(new Date());
                // 4 更新订单数据到数据库
                orderMapper.updateByPrimaryKey(order);
                log.info("更改订单支付状态:已支付");
            } else {
                CastException.cast(ShopCode.SHOP_REQUEST_PARAMETER_VALID);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("更改订单支付状态失败");
        }
    }
}
