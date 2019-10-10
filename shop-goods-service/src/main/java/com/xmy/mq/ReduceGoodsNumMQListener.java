package com.xmy.mq;

import com.alibaba.fastjson.JSON;
import com.xmy.constant.ShopCode;
import com.xmy.exception.CastException;
import com.xmy.mapper.ShopGoodsMapper;
import com.xmy.pojo.ShopGoods;
import com.xmy.pojo.ShopOrderGoodsLog;
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
@RocketMQMessageListener(topic = "${mq.goods.topic}",
        consumerGroup = "${mq.goods.consumer.group.name}",
        messageModel = MessageModel.CLUSTERING)
public class ReduceGoodsNumMQListener implements RocketMQListener<MessageExt> {

    @Autowired
    private ShopGoodsMapper shopGoodsMapper;

    @Override
    public void onMessage(MessageExt messageExt) {
        String body = new String(messageExt.getBody());
        ShopOrderGoodsLog orderGoodsLog = JSON.parseObject(body, ShopOrderGoodsLog.class);

        ShopGoods goods = shopGoodsMapper.selectByPrimaryKey(orderGoodsLog.getGoodsId());
        // 判断库存是否充足
        if (goods.getGoodsNumber() < orderGoodsLog.getGoodsNumber()) {
            CastException.cast(ShopCode.SHOP_REDUCE_GOODS_NUM_FAIL);
        }
        Integer goodsnum = goods.getGoodsNumber();
        goods.setGoodsNumber(goods.getGoodsNumber() - orderGoodsLog.getGoodsNumber());
        shopGoodsMapper.updateByPrimaryKey(goods);

    }
}
