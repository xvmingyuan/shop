package com.xmy.mq;

import com.alibaba.fastjson.JSON;
import com.xmy.constant.ShopCode;
import com.xmy.entity.MQEntity;
import com.xmy.mapper.ShopCouponMapper;
import com.xmy.pojo.ShopCoupon;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author xmy
 * @date 2019-10-09 21:42
 */
@SuppressWarnings("ALL")
@Slf4j
@Component
@RocketMQMessageListener(topic = "${mq.order.topic}",
        consumerGroup = "${mq.order.consumer.group.name}",
        messageModel = MessageModel.BROADCASTING)
public class CancelMQListener implements RocketMQListener<MessageExt> {

    @Autowired
    private ShopCouponMapper couponMapper;

    @Override
    public void onMessage(MessageExt messageExt) {
        try {
            //1 解析消息
            String body = new String(messageExt.getBody(), "UTF-8");
            MQEntity mqEntity = JSON.parseObject(body, MQEntity.class);
            log.info("优惠券回退服务,接收到信息");
            if (mqEntity.getCouponId() != null) {
                //2 查询优惠券信息(消息幂等性)
                ShopCoupon coupon = couponMapper.selectByPrimaryKey(mqEntity.getCouponId());
                //3 更新优惠券状态
                coupon.setUsedTime(null);
                coupon.setIsUsed(ShopCode.SHOP_COUPON_UNUSED.getCode());
                coupon.setOrderId(null);
                couponMapper.updateByPrimaryKey(coupon);
                log.info("回退优惠券成功");
            } else {
                log.info("未使用优惠券");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("回退优惠券失败");
        }


    }
}
