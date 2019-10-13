package com.xmy.listener;

import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Component;

@Component
public class OrderTransactionMQListenerImpl implements TransactionListener {
    @Override
    public LocalTransactionState executeLocalTransaction(Message message, Object object) {
        System.out.println(message);
        System.out.println();
        String tags = (String) message.getTags();
        System.out.println("tags: " + tags);

        String topic = (String) message.getTopic();
        System.out.println("topic: " + topic);

        String kes = (String) message.getKeys();
        System.out.println("kes: " + kes);

        String transaction_id = message.getTransactionId();
        System.out.println("transaction_id: " + transaction_id);

        String args = (String) object;
        System.out.println("自定义args:" + args);


        if (StringUtils.equals(message.getTags(), "tag0")) {
            return LocalTransactionState.ROLLBACK_MESSAGE;
        } else if (StringUtils.equals("tag1", message.getTags())) {
            return LocalTransactionState.COMMIT_MESSAGE;
        } else if (StringUtils.equals("tag2", message.getTags())) {
            return LocalTransactionState.UNKNOW;
        }
        return LocalTransactionState.UNKNOW;
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
