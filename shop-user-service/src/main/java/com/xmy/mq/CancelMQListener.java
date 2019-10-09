package com.xmy.mq;

import com.alibaba.fastjson.JSON;
import com.xmy.api.IUserService;
import com.xmy.constant.ShopCode;
import com.xmy.entity.MQEntity;
import com.xmy.pojo.ShopUserMoneyLog;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
@RocketMQMessageListener(topic = "${mq.order.topic}",
        consumerGroup = "${mq.order.consumer.group.name}",
        messageModel = MessageModel.BROADCASTING)
public class CancelMQListener implements RocketMQListener<MessageExt> {

    @Autowired
    private IUserService userService;

    @Override
    public void onMessage(MessageExt messageExt) {
        try {
            // 1 解析消息
            String body = new String(messageExt.getBody(), "UTF-8");
            MQEntity mqEntity = JSON.parseObject(body, MQEntity.class);
            log.info("用户服务,接受到消息");
            // 如果有使用余额,才回退余额
            if (mqEntity.getUserMoney() != null && mqEntity.getUserMoney().compareTo(BigDecimal.ZERO) > 0) {
                //2 调用业务层处理 回退余额
                ShopUserMoneyLog userMoneyLog = new ShopUserMoneyLog();
                userMoneyLog.setUserId(mqEntity.getUserId());
                userMoneyLog.setUseMoney(mqEntity.getUserMoney());
                userMoneyLog.setOrderId(mqEntity.getOrderId());
                userMoneyLog.setMoneyLogType(ShopCode.SHOP_USER_MONEY_REFUND.getCode());
                userService.updateMoneyPaid(userMoneyLog);
                log.info("余额回退成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("余额回退失败");
        }


    }
}
