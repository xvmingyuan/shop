package com.xmy.listener;

import com.alibaba.fastjson.JSON;
import com.xmy.mapper.ShopOrderMapper;
import com.xmy.mapper.ShopOrderMqStatusLogMapper;
import com.xmy.pojo.ShopOrder;
import com.xmy.pojo.ShopOrderMqStatusLog;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@SuppressWarnings("ALL")
@Component
@Slf4j
public class OrderTransactionMQListenerImpl implements TransactionListener {
    @Autowired
    private ShopOrderMapper orderMapper;

    @Autowired
    private ShopOrderMqStatusLogMapper orderMqStatusLogMapper;

    @Value("${mq.order.confirm.tag.confirm}")
    private String orderConfirmTag;

    @Override
    public LocalTransactionState executeLocalTransaction(Message message, Object object) {
        String tags = message.getTags();
        if (StringUtils.equals(tags, orderConfirmTag)) {
            String body = new String(message.getBody());
            try {
                ShopOrder order = JSON.parseObject(body, ShopOrder.class);
                orderMapper.insert(order);
                ShopOrderMqStatusLog orderMqStatusLog = new ShopOrderMqStatusLog();
                orderMqStatusLog.setOrderId(order.getOrderId());
                orderMqStatusLogMapper.insert(orderMqStatusLog);
                log.info("订单" + order.getOrderId() + ",下单成功");
                return LocalTransactionState.COMMIT_MESSAGE;
            } catch (Exception e) {
                e.printStackTrace();
                return LocalTransactionState.ROLLBACK_MESSAGE;
            }
        } else {
            return LocalTransactionState.UNKNOW;
        }
    }


    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {
        System.out.println("消息Tag: " + messageExt.getTags());
        return LocalTransactionState.COMMIT_MESSAGE;
//        System.out.println("检查 checkLocalTransaction" + Thread.currentThread().getId());
//        System.out.println(messageExt);
//        String tags = messageExt.getTags();
//        System.out.println("tags: " + tags);
//        if (tags.equals("0")) {
//            String payload = new String(messageExt.getBody());
//            System.out.println("消息体 检查" + payload);
//            return LocalTransactionState.ROLLBACK_MESSAGE;
//        }
//        if (tags.equals("1")) {
//            String payload = new String(messageExt.getBody());
//            System.out.println("消息体 检查" + payload);
//            return LocalTransactionState.COMMIT_MESSAGE;
//        }
//        return LocalTransactionState.COMMIT_MESSAGE;
    }
}
