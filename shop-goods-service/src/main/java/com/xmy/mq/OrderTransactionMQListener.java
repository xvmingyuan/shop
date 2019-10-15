package com.xmy.mq;

import com.alibaba.fastjson.JSON;
import com.xmy.constant.ShopCode;
import com.xmy.entity.OrderResult;
import com.xmy.entity.Result;
import com.xmy.mapper.ShopGoodsMapper;
import com.xmy.mapper.ShopOrderGoodsLogMapper;
import com.xmy.pojo.ShopGoods;
import com.xmy.pojo.ShopGoodsExample;
import com.xmy.pojo.ShopOrder;
import com.xmy.pojo.ShopOrderGoodsLog;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@SuppressWarnings("ALL")
@Slf4j
@Component
@RocketMQMessageListener(topic = "${mq.order.confirm.topic}",
        consumerGroup = "${mq.order.confirm.consumer.groupname}",
        messageModel = MessageModel.BROADCASTING)
public class OrderTransactionMQListener implements RocketMQListener<MessageExt> {
    @Autowired
    private ShopGoodsMapper shopGoodsMapper;

    @Autowired
    private ShopOrderGoodsLogMapper shopOrderGoodsLogMapper;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Value("${mq.order.confirm.consumer.callback.topic}")
    private String callbackTopic;

    @Value("${mq.order.confirm.consumer.callback.tag}")
    private String callbackTag;

    @Value("${mq.order.confirm.consumer.callback.sourcecode}")
    private String sourceCode;

    @Override
    public void onMessage(MessageExt messageExt) {
        String body = new String(messageExt.getBody());
        ShopOrder order = JSON.parseObject(body, ShopOrder.class);
        log.info("商品扣减服务,接收到信息");
        // 订单ID  商品ID 商品数量
        ShopOrderGoodsLog orderGoodsLog = new ShopOrderGoodsLog();
        orderGoodsLog.setOrderId(order.getOrderId());
        orderGoodsLog.setGoodsId(order.getGoodsId());
        orderGoodsLog.setGoodsNumber(order.getGoodsNumber());
        Result result = reduceGoodsNum(orderGoodsLog);
        try {
            sendMessage(result);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Result reduceGoodsNum(ShopOrderGoodsLog orderGoodsLog) {
        // 空指针过滤
        if (orderGoodsLog == null ||
                orderGoodsLog.getOrderId() == null ||
                orderGoodsLog.getGoodsId() == null ||
                orderGoodsLog.getGoodsNumber() == null ||
                orderGoodsLog.getGoodsNumber().intValue() <= 0) {
            OrderResult orderResult = new OrderResult();
            orderResult.setOrderId(orderGoodsLog.getOrderId());
            orderResult.setStatus(ShopCode.SHOP_FAIL.getSuccess());
            orderResult.setMessage(ShopCode.SHOP_REQUEST_PARAMETER_VALID.getMessage()); // JSON.toJSONString(orderResult)
            orderResult.setSourceCode(sourceCode);
            return new Result(ShopCode.SHOP_FAIL.getSuccess(), ShopCode.SHOP_REQUEST_PARAMETER_VALID.getCode(), JSON.toJSONString(orderResult));
        }


        /** 乐观锁实现*/
        ShopGoods goods = shopGoodsMapper.selectByPrimaryKey(orderGoodsLog.getGoodsId());
        // 判断库存是否充足
        if (goods.getGoodsNumber() < orderGoodsLog.getGoodsNumber()) {
            OrderResult orderResult = new OrderResult();
            orderResult.setOrderId(orderGoodsLog.getOrderId());
            orderResult.setStatus(ShopCode.SHOP_FAIL.getSuccess());
            orderResult.setMessage(ShopCode.SHOP_GOODS_NUM_NOT_ENOUGH.getMessage());
            orderResult.setSourceCode(sourceCode);
            return new Result(ShopCode.SHOP_FAIL.getSuccess(), ShopCode.SHOP_GOODS_NUM_NOT_ENOUGH.getCode(), JSON.toJSONString(orderResult));
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
            log.info("库存数量并发修改,处理...");
            goods = shopGoodsMapper.selectByPrimaryKey(orderGoodsLog.getGoodsId());
            if (goods.getGoodsNumber() < orderGoodsLog.getGoodsNumber()) {
                OrderResult orderResult = new OrderResult();
                orderResult.setOrderId(orderGoodsLog.getOrderId());
                orderResult.setStatus(ShopCode.SHOP_FAIL.getSuccess());
                orderResult.setMessage(ShopCode.SHOP_GOODS_NUM_NOT_ENOUGH.getMessage());
                orderResult.setSourceCode(sourceCode);
                return new Result(ShopCode.SHOP_FAIL.getSuccess(), ShopCode.SHOP_GOODS_NUM_NOT_ENOUGH.getCode(), JSON.toJSONString(orderResult));
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
        log.info("扣减库存成功");
        OrderResult orderResult = new OrderResult();
        orderResult.setOrderId(orderGoodsLog.getOrderId());
        orderResult.setStatus(ShopCode.SHOP_SUCCESS.getSuccess());
        orderResult.setMessage(ShopCode.SHOP_SUCCESS.getMessage());
        orderResult.setSourceCode(sourceCode);
        return new Result(ShopCode.SHOP_SUCCESS.getSuccess(), ShopCode.SHOP_SUCCESS.getCode(), JSON.toJSONString(orderResult));
    }

    private void sendMessage(Result result) throws Exception {
        Message message = new Message(callbackTopic, callbackTag, result.getCode().toString(), JSON.toJSONString(result).getBytes());
        rocketMQTemplate.getProducer().send(message);
        log.info("库存扣减成功,发送消息");

    }
}
