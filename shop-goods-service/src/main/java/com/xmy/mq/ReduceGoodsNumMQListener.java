package com.xmy.mq;

import com.alibaba.fastjson.JSON;
import com.xmy.constant.ShopCode;
import com.xmy.exception.CastException;
import com.xmy.mapper.ShopGoodsMapper;
import com.xmy.mapper.ShopOrderGoodsLogMapper;
import com.xmy.pojo.ShopGoods;
import com.xmy.pojo.ShopGoodsExample;
import com.xmy.pojo.ShopOrderGoodsLog;
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
@RocketMQMessageListener(topic = "${mq.goods.topic}",
        consumerGroup = "${mq.goods.consumer.group.name}",
        messageModel = MessageModel.CLUSTERING)
public class ReduceGoodsNumMQListener implements RocketMQListener<MessageExt> {

    @Autowired
    private ShopGoodsMapper shopGoodsMapper;

    @Autowired
    private ShopOrderGoodsLogMapper shopOrderGoodsLogMapper;

    @Override
    public void onMessage(MessageExt messageExt) {
        String body = new String(messageExt.getBody());
        ShopOrderGoodsLog orderGoodsLog = JSON.parseObject(body, ShopOrderGoodsLog.class);
        log.info("商品扣减服务,接受到消息");
        try {
            // 空指针过滤
            if (orderGoodsLog == null ||
                    orderGoodsLog.getOrderId() == null ||
                    orderGoodsLog.getGoodsId() == null ||
                    orderGoodsLog.getGoodsNumber() == null ||
                    orderGoodsLog.getGoodsNumber().intValue() <= 0) {
                CastException.cast(ShopCode.SHOP_REQUEST_PARAMETER_VALID);
            }

            /** 乐观锁实现*/
            ShopGoods goods = shopGoodsMapper.selectByPrimaryKey(orderGoodsLog.getGoodsId());
            // 判断库存是否充足
            if (goods.getGoodsNumber() < orderGoodsLog.getGoodsNumber()) {
                CastException.cast(ShopCode.SHOP_GOODS_NUM_NOT_ENOUGH);
            }
            Integer goodsNumber = goods.getGoodsNumber();
            goods.setGoodsNumber(goods.getGoodsNumber() - orderGoodsLog.getGoodsNumber());
            // 分布式并发问题 ,使用乐观锁 <方案待提升>
            ShopGoodsExample shopGoodsExample = new ShopGoodsExample();
            ShopGoodsExample.Criteria criteria = shopGoodsExample.createCriteria();
            criteria.andGoodsIdEqualTo(goods.getGoodsId());
            criteria.andGoodsNumberEqualTo(goodsNumber);
            int r = shopGoodsMapper.updateByExample(goods, shopGoodsExample);
            while (r <= 0) {
                // 未修改成功
                log.info("MQ库存数量并发修改,处理...");
                goods = shopGoodsMapper.selectByPrimaryKey(orderGoodsLog.getGoodsId());
                if (goods.getGoodsNumber() < orderGoodsLog.getGoodsNumber()) {
                    CastException.cast(ShopCode.SHOP_GOODS_NUM_NOT_ENOUGH);
                } else {
                    goodsNumber = goods.getGoodsNumber();
                    goods.setGoodsNumber(goods.getGoodsNumber() - orderGoodsLog.getGoodsNumber());
                    shopGoodsExample = new ShopGoodsExample();
                    criteria = shopGoodsExample.createCriteria();
                    criteria.andGoodsIdEqualTo(goods.getGoodsId());
                    criteria.andGoodsNumberEqualTo(goodsNumber);
                    r = shopGoodsMapper.updateByExample(goods, shopGoodsExample);

                }
            }

            // 记录库存日志 库存数量  负数:扣库存
            orderGoodsLog.setGoodsNumber(-(orderGoodsLog.getGoodsNumber()));
            orderGoodsLog.setLogTime(new Date());
            shopOrderGoodsLogMapper.insert(orderGoodsLog);
            log.info("MQ扣减库存成功");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
