package com.xmy.mq;

import com.alibaba.fastjson.JSON;
import com.xmy.constant.ShopCode;
import com.xmy.entity.MQEntity;
import com.xmy.mapper.ShopGoodsMapper;
import com.xmy.mapper.ShopMsgConsumerMapper;
import com.xmy.mapper.ShopOrderGoodsLogMapper;
import com.xmy.pojo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@SuppressWarnings("ALL")
@Slf4j
@Component
@RocketMQMessageListener(topic = "${mq.order.topic}",
        consumerGroup = "${mq.order.consumer.group.name}",
        messageModel = MessageModel.BROADCASTING)
public class CancelMQListener implements RocketMQListener<MessageExt> {
    @Value("${mq.order.consumer.group.name}")
    private String groupName;

    @Autowired
    private ShopMsgConsumerMapper msgConsumerMapper;

    @Autowired
    private ShopGoodsMapper goodsMapper;

    @Autowired
    private ShopOrderGoodsLogMapper orderGoodsLogMapper;

    @Override
    public void onMessage(MessageExt messageExt) {
        String msgId = null;
        String tags = null;
        String keys = null;
        String body = null;
        try {
            //1 解析消息内容
            msgId = messageExt.getMsgId();
            tags = messageExt.getTags();
            keys = messageExt.getKeys();
            body = new String(messageExt.getBody(), "UTF-8");

            log.info("商品服务,接收到信息");

            //2 查询消息记录
            ShopMsgConsumerKey msgConsumerKey = new ShopMsgConsumerKey();
            msgConsumerKey.setMsgKey(keys);
            msgConsumerKey.setMsgTag(tags);
            msgConsumerKey.setGroupName(groupName);
            ShopMsgConsumer msgConsumer = msgConsumerMapper.selectByPrimaryKey(msgConsumerKey);
            //3 判断如果消费过..
            if (msgConsumer != null) {
                //3.1 获取消息状态
                Integer status = msgConsumer.getConsumerStatus();
                // 处理过...返回
                if (ShopCode.SHOP_MQ_MESSAGE_STATUS_SUCCESS.getCode().intValue() == status.intValue()) {
                    log.info("消息:" + msgId + ",已处理过");
                    return;
                }
                // 正在处理..
                if (ShopCode.SHOP_MQ_MESSAGE_STATUS_PROCESSING.getCode().intValue() == status.intValue()) {
                    log.info("消息:" + msgId + ",正在处理");
                    return;
                }

                // 处理失败..
                if (ShopCode.SHOP_MQ_MESSAGE_STATUS_FAIL.getCode().intValue() == status.intValue()) {
                    // 获取消息处理次数
                    Integer times = msgConsumer.getConsumerTimes();
                    if (times > 3) {
                        log.info("消息:" + msgId + ",消息处理超过3次,不能再进行处理了");
                        return;
                    }
                    msgConsumer.setConsumerStatus(ShopCode.SHOP_MQ_MESSAGE_STATUS_PROCESSING.getCode());

                    // 使用数据库乐观锁更新
                    ShopMsgConsumerExample shopMsgConsumerExample = new ShopMsgConsumerExample();
                    ShopMsgConsumerExample.Criteria criteria = shopMsgConsumerExample.createCriteria();
                    criteria.andMsgTagEqualTo(msgConsumer.getMsgTag());
                    criteria.andMsgKeyEqualTo(msgConsumer.getMsgKey());
                    criteria.andGroupNameEqualTo(msgConsumer.getGroupName());
                    criteria.andConsumerTimesEqualTo(msgConsumer.getConsumerTimes());
                    int r = msgConsumerMapper.updateByExampleSelective(msgConsumer, shopMsgConsumerExample);
                    if (r <= 0) {
                        // 未修改成功
                        log.info("并发修改,稍后处理");
                    }
                }
            } else { //4 判断如果没消费过...
                msgConsumer = new ShopMsgConsumer();
                msgConsumer.setMsgTag(tags);
                msgConsumer.setMsgKey(keys);
                msgConsumer.setMsgId(msgId);
                msgConsumer.setGroupName(groupName);
                msgConsumer.setConsumerStatus(ShopCode.SHOP_MQ_MESSAGE_STATUS_PROCESSING.getCode());
                msgConsumer.setMsgBody(body);
                msgConsumer.setConsumerTimes(0);

                // 将消息处理信息添加到数据库
                msgConsumerMapper.insert(msgConsumer);
            }
            //5 回退库存
            MQEntity mqEntity = JSON.parseObject(body, MQEntity.class);
            ShopGoods goods = goodsMapper.selectByPrimaryKey(mqEntity.getGoodsId());
            goods.setGoodsNumber(goods.getGoodsNumber() + mqEntity.getGoodsNumber());
            goodsMapper.updateByPrimaryKey(goods);
            // 记录库存操作日志
            ShopOrderGoodsLog orderGoodsLog = new ShopOrderGoodsLog();
            orderGoodsLog.setGoodsId(mqEntity.getGoodsId());
            orderGoodsLog.setOrderId(mqEntity.getOrderId());
            orderGoodsLog.setGoodsNumber(mqEntity.getGoodsNumber());
            orderGoodsLog.setLogTime(new Date());
            orderGoodsLogMapper.insert(orderGoodsLog);

            //6 记录消费消息日志
            msgConsumer.setConsumerStatus(ShopCode.SHOP_MQ_MESSAGE_STATUS_SUCCESS.getCode());
            msgConsumer.setConsumerTimestamp(new Date());
            msgConsumerMapper.updateByPrimaryKey(msgConsumer);

            log.info("回退库存成功");

        } catch (Exception e) {
            e.printStackTrace();
            ShopMsgConsumerKey msgConsumerKey = new ShopMsgConsumerKey();
            msgConsumerKey.setMsgKey(keys);
            msgConsumerKey.setMsgTag(tags);
            msgConsumerKey.setGroupName(groupName);
            ShopMsgConsumer msgConsumer = msgConsumerMapper.selectByPrimaryKey(msgConsumerKey);
            if (msgConsumer == null) {
                // 数据库未记录
                msgConsumer = new ShopMsgConsumer();
                msgConsumer.setMsgTag(tags);
                msgConsumer.setMsgKey(keys);
                msgConsumer.setMsgId(msgId);
                msgConsumer.setMsgBody(body);
                msgConsumer.setGroupName(groupName);
                msgConsumer.setConsumerStatus(ShopCode.SHOP_MQ_MESSAGE_STATUS_FAIL.getCode());
                msgConsumer.setConsumerTimes(1);
                msgConsumerMapper.insert(msgConsumer);
            } else {
                msgConsumer.setConsumerTimes(msgConsumer.getConsumerTimes() + 1);
                msgConsumerMapper.updateByPrimaryKey(msgConsumer);
            }
        }
    }
}
