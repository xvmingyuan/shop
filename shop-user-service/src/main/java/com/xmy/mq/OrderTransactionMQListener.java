package com.xmy.mq;

import com.alibaba.fastjson.JSON;
import com.xmy.constant.ShopCode;
import com.xmy.entity.OrderResult;
import com.xmy.entity.Result;
import com.xmy.mapper.ShopUserMapper;
import com.xmy.mapper.ShopUserMoneyLogMapper;
import com.xmy.pojo.ShopOrder;
import com.xmy.pojo.ShopUser;
import com.xmy.pojo.ShopUserMoneyLog;
import com.xmy.pojo.ShopUserMoneyLogExample;
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

import java.math.BigDecimal;
import java.util.Date;

@SuppressWarnings("ALL")
@Slf4j
@Component
@RocketMQMessageListener(topic = "${mq.order.confirm.topic}",
        consumerGroup = "${mq.order.confirm.consumer.groupname}",
        messageModel = MessageModel.BROADCASTING)
public class OrderTransactionMQListener implements RocketMQListener<MessageExt> {
    @Autowired
    private ShopUserMapper userMapper;

    @Autowired
    private ShopUserMoneyLogMapper userMoneyLogMapper;

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
        log.info("用户余额扣减服务,接受到消息");
        Result result = null;
        if (order.getMoneyPaid() != null && order.getMoneyPaid().compareTo(BigDecimal.ZERO) == 1) {
            ShopUserMoneyLog userMoneyLog = new ShopUserMoneyLog();
            userMoneyLog.setOrderId(order.getOrderId());
            userMoneyLog.setUserId(order.getUserId());
            userMoneyLog.setUseMoney(order.getMoneyPaid());
            userMoneyLog.setMoneyLogType(ShopCode.SHOP_USER_MONEY_PAID.getCode());
            result = updateMoneyPaid(userMoneyLog);
        } else {
            OrderResult orderResult = new OrderResult();
            orderResult.setOrderId(order.getOrderId());
            orderResult.setStatus(ShopCode.SHOP_SUCCESS.getSuccess());
            orderResult.setMessage(ShopCode.SHOP_SUCCESS.getMessage());
            orderResult.setSourceCode(sourceCode);
            result = new Result(ShopCode.SHOP_SUCCESS.getSuccess(), ShopCode.SHOP_SUCCESS.getCode(), JSON.toJSONString(orderResult));
        }

        try {
            sendMessage(result);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Result updateMoneyPaid(ShopUserMoneyLog userMoneyLog) {
        OrderResult orderResult = new OrderResult();
        //1校验参数是否合法
        if (userMoneyLog == null ||
                userMoneyLog.getUserId() == null ||
                userMoneyLog.getOrderId() == null ||
                userMoneyLog.getUseMoney() == null ||
                userMoneyLog.getUseMoney().compareTo(BigDecimal.ZERO) <= 0) {
            orderResult = new OrderResult();
            orderResult.setOrderId(userMoneyLog.getOrderId());
            orderResult.setStatus(ShopCode.SHOP_FAIL.getSuccess());
            orderResult.setMessage(ShopCode.SHOP_REQUEST_PARAMETER_VALID.getMessage()); // JSON.toJSONString(orderResult)
            orderResult.setSourceCode(sourceCode);
            return new Result(ShopCode.SHOP_FAIL.getSuccess(), ShopCode.SHOP_REQUEST_PARAMETER_VALID.getCode(), JSON.toJSONString(orderResult));
        }

        //2查询订单余额使用日志
        ShopUserMoneyLogExample userMoneyLogExample = new ShopUserMoneyLogExample();
        ShopUserMoneyLogExample.Criteria criteria = userMoneyLogExample.createCriteria();
        criteria.andOrderIdEqualTo(userMoneyLog.getOrderId());
        criteria.andUserIdEqualTo(userMoneyLog.getUserId());
        long r = userMoneyLogMapper.countByExample(userMoneyLogExample);

        ShopUser user = userMapper.selectByPrimaryKey(userMoneyLog.getUserId());

        //3扣减余额
        if (userMoneyLog.getMoneyLogType().intValue() == ShopCode.SHOP_USER_MONEY_PAID.getCode().intValue()) {
            if (r > 0) {//已付款
                orderResult = new OrderResult();
                orderResult.setOrderId(userMoneyLog.getOrderId());
                orderResult.setStatus(ShopCode.SHOP_FAIL.getSuccess());
                orderResult.setMessage(ShopCode.SHOP_ORDER_PAY_STATUS_IS_PAY.getMessage());
                orderResult.setSourceCode(sourceCode);
                return new Result(ShopCode.SHOP_FAIL.getSuccess(), ShopCode.SHOP_ORDER_PAY_STATUS_IS_PAY.getCode(), JSON.toJSONString(orderResult));
            }
            //扣减余额
            user.setUserMoney(user.getUserMoney().subtract(userMoneyLog.getUseMoney()));
            userMapper.updateByPrimaryKey(user);
        }

        //4回退余额
        if (userMoneyLog.getMoneyLogType().intValue() == ShopCode.SHOP_USER_MONEY_REFUND.getCode().intValue()) {
            if (r < 0) {//未付款
                orderResult = new OrderResult();
                orderResult.setOrderId(userMoneyLog.getOrderId());
                orderResult.setStatus(ShopCode.SHOP_FAIL.getSuccess());
                orderResult.setMessage(ShopCode.SHOP_ORDER_PAY_STATUS_NO_PAY.getMessage());
                orderResult.setSourceCode(sourceCode);
                return new Result(ShopCode.SHOP_FAIL.getSuccess(), ShopCode.SHOP_ORDER_PAY_STATUS_NO_PAY.getCode(), JSON.toJSONString(orderResult));
            }
            //防止多次退款
            ShopUserMoneyLogExample userMoneyLogExample1 = new ShopUserMoneyLogExample();
            ShopUserMoneyLogExample.Criteria criteria1 = userMoneyLogExample1.createCriteria();
            criteria1.andUserIdEqualTo(userMoneyLog.getUserId());
            criteria1.andOrderIdEqualTo(userMoneyLog.getOrderId());
            criteria1.andMoneyLogTypeEqualTo(ShopCode.SHOP_USER_MONEY_REFUND.getCode());
            long r2 = userMoneyLogMapper.countByExample(userMoneyLogExample1);
            if (r2 > 0) {//已退过款
                orderResult = new OrderResult();
                orderResult.setOrderId(userMoneyLog.getOrderId());
                orderResult.setStatus(ShopCode.SHOP_FAIL.getSuccess());
                orderResult.setMessage(ShopCode.SHOP_USER_MONEY_REDUCE_ALREADY.getMessage());
                orderResult.setSourceCode(sourceCode);
                return new Result(ShopCode.SHOP_FAIL.getSuccess(), ShopCode.SHOP_USER_MONEY_REDUCE_ALREADY.getCode(), JSON.toJSONString(orderResult));
            }
            // 退款加余额
            user.setUserMoney(user.getUserMoney().add(userMoneyLog.getUseMoney()));
            userMapper.updateByPrimaryKey(user);
        }

        //5记录订单余额使用日志
        userMoneyLog.setCreateTime(new Date());
        Result result;
        try {
            userMoneyLogMapper.insert(userMoneyLog);
            orderResult = new OrderResult();
            orderResult.setMessage(ShopCode.SHOP_USER_MONEY_REDUCE_SUCCESS.getMessage());
            orderResult.setOrderId(userMoneyLog.getOrderId());
            orderResult.setStatus(ShopCode.SHOP_SUCCESS.getSuccess());
            orderResult.setSourceCode(sourceCode);
            result = new Result(ShopCode.SHOP_SUCCESS.getSuccess(), ShopCode.SHOP_USER_MONEY_REDUCE_SUCCESS.getCode(), JSON.toJSONString(orderResult));
        } catch (Exception e) {
            log.info(e.toString());
            orderResult = new OrderResult();
            orderResult.setMessage(ShopCode.SHOP_USER_MONEY_REDUCE_SUCCESS.getMessage());
            orderResult.setOrderId(userMoneyLog.getOrderId());
            orderResult.setStatus(ShopCode.SHOP_FAIL.getSuccess());
            orderResult.setMessage(ShopCode.SHOP_USER_MONEY_REDUCE_ALREADY.getMessage());
            orderResult.setSourceCode(sourceCode);
            result = new Result(ShopCode.SHOP_FAIL.getSuccess(), ShopCode.SHOP_USER_MONEY_REDUCE_FAIL.getCode(), ShopCode.SHOP_USER_MONEY_REDUCE_FAIL.getMessage());
        }
        return result;
    }

    private void sendMessage(Result result) throws Exception {
        Message message = new Message(callbackTopic, callbackTag, result.getCode().toString(), JSON.toJSONString(result).getBytes());
        rocketMQTemplate.getProducer().send(message);
        log.info("余额扣减成功,发送消息");

    }
}
