package com.xmy.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.xmy.api.IPayService;
import com.xmy.constant.ShopCode;
import com.xmy.entity.PayResult;
import com.xmy.entity.Result;
import com.xmy.exception.CastException;
import com.xmy.mapper.ShopMsgProviderMapper;
import com.xmy.mapper.ShopPayMapper;
import com.xmy.pojo.ShopMsgProvider;
import com.xmy.pojo.ShopMsgProviderKey;
import com.xmy.pojo.ShopPay;
import com.xmy.pojo.ShopPayExample;
import com.xmy.utils.IDWorker;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;


@SuppressWarnings("ALL")
@Slf4j
@Component
@Service(interfaceClass = IPayService.class)
public class PayServiceImpl implements IPayService {

    @Autowired
    private ShopPayMapper payMapper;

    @Autowired
    private ShopMsgProviderMapper msgProviderMapper;

    @Autowired
    private IDWorker idWorker;

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Value("${rocketmq.producer.group}")
    private String groupName;

    @Value("${mq.pay.topic}")
    private String topic;

    @Value("${mq.pay.tag}")
    private String tag;


    @Override
    public Result createPayment(ShopPay shopPay) {
        if (shopPay == null || shopPay.getOrderId() == null) {
            CastException.cast(ShopCode.SHOP_REQUEST_PARAMETER_VALID);
        }
        //1 判断订单支付状态
        ShopPayExample payExample = new ShopPayExample();
        ShopPayExample.Criteria criteria = payExample.createCriteria();
        criteria.andOrderIdEqualTo(shopPay.getOrderId());
        criteria.andIsPaidEqualTo(ShopCode.SHOP_ORDER_PAY_STATUS_IS_PAY.getCode());
        long r = payMapper.countByExample(payExample);
        if (r > 0) {// 已付款
            CastException.cast(ShopCode.SHOP_PAYMENT_IS_PAID);
        }
        //2 设置订单的状态为未支付
        shopPay.setIsPaid(ShopCode.SHOP_ORDER_PAY_STATUS_NO_PAY.getCode());
        shopPay.setPayId(idWorker.nextId());
        //3 保存支付订单
        Result result;
        try {
            payMapper.insert(shopPay);
            PayResult payResult = new PayResult();
            payResult.setMessage(ShopCode.SHOP_ORDER_PAY_STATUS_NO_PAY.getMessage());
            payResult.setPayId(shopPay.getPayId());
            payResult.setOrderId(shopPay.getOrderId());
            payResult.setStatus(ShopCode.SHOP_SUCCESS.getSuccess());
            result = new Result(ShopCode.SHOP_SUCCESS.getSuccess(), JSON.toJSONString(payResult));
            log.info("支付服务,支付订单创建成功");
            return result;
        } catch (Exception e) {
            //返回失败状态
            e.printStackTrace();
            log.info("支付服务,支付订单创建失败");
            result = new Result(ShopCode.SHOP_FAIL.getSuccess(), ShopCode.SHOP_FAIL.getMessage());
            return result;
        }
    }

    @Override
    public Result callbackPayment(ShopPay shopPay) {

        Result result;
        log.info("支付服务,支付回调");
        try {
            //1 判断用户支付状态
            if (shopPay.getIsPaid().intValue() == ShopCode.SHOP_ORDER_PAY_STATUS_IS_PAY.getCode().intValue()) {
                //2 更新支付订单状态为已支付
                Long payId = shopPay.getPayId();
                ShopPay pay = payMapper.selectByPrimaryKey(payId);
                // 判断支付是否存在
                if (pay == null) {
                    CastException.cast(ShopCode.SHOP_PAYMENT_NOT_FOUND);
                }
                pay.setIsPaid(ShopCode.SHOP_ORDER_PAY_STATUS_IS_PAY.getCode());
                //3 创建支付成功的消息
                int r = payMapper.updateByPrimaryKeySelective(pay);
                log.info("支付服务,支付状态:已支付");
                if (r == 1) {
                    //4 将消息持久化到数据库
                    ShopMsgProvider msgProvider = new ShopMsgProvider();
                    msgProvider.setId(String.valueOf(idWorker.nextId()));
                    msgProvider.setGroupName(groupName);
                    msgProvider.setMsgTopic(topic);
                    msgProvider.setMsgTag(tag);
                    msgProvider.setMsgKey(String.valueOf(pay.getPayId()));
                    msgProvider.setMsgBody(JSON.toJSONString(shopPay));
                    msgProvider.setCreateTime(new Date());
                    msgProviderMapper.insert(msgProvider);
                    log.info("支付服务,持久化消息到库");

                    // 在线程池中进行处理
                    threadPoolTaskExecutor.submit(new Runnable() {
                        @Override
                        public void run() {
                            //5 发送消息到MQ,有延迟和堆积,使用线程异步优化
                            SendResult sendResult = null;
                            try {
                                sendResult = sendMessage(topic, tag, String.valueOf(pay.getPayId()), JSON.toJSONString(shopPay));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (sendResult.getSendStatus().equals(SendStatus.SEND_OK)) {
                                //6 等待发送结果,如果MQ接受到消息,删除发送成功的消息
                                log.info("支付服务,消息发送成功");
                                ShopMsgProviderKey msgProviderKey = new ShopMsgProviderKey();
                                msgProviderKey.setGroupName(groupName);
                                msgProviderKey.setMsgKey(String.valueOf(pay.getPayId()));
                                msgProviderKey.setMsgTag(tag);
                                msgProviderMapper.deleteByPrimaryKey(msgProviderKey);
                                log.info("支付服务,数据库中持久化消息已删除");
                            }
                        }
                    });
                }
                PayResult payResult = new PayResult();
                payResult.setMessage(ShopCode.SHOP_ORDER_PAY_STATUS_IS_PAY.getMessage());
                payResult.setPayId(pay.getPayId());
                payResult.setOrderId(shopPay.getOrderId());
                payResult.setStatus(ShopCode.SHOP_SUCCESS.getSuccess());

                result = new Result(ShopCode.SHOP_SUCCESS.getSuccess(), JSON.toJSONString(payResult));
                return result;
            } else {
                CastException.cast(ShopCode.SHOP_PAYMENT_PAY_ERROR);
                result = new Result(ShopCode.SHOP_FAIL.getSuccess(), ShopCode.SHOP_FAIL.getMessage());
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = new Result(ShopCode.SHOP_FAIL.getSuccess(), ShopCode.SHOP_FAIL.getMessage());
            return result;
        }
    }

    /**
     * 发送支付成功消息
     *
     * @param topic
     * @param tag
     * @param key
     * @param body
     */
    private SendResult sendMessage(String topic, String tag, String key, String body) throws Exception {
        if (StringUtils.isEmpty(topic)) {
            CastException.cast(ShopCode.SHOP_MQ_TOPIC_IS_EMPTY);
        }
        if (StringUtils.isEmpty(body)) {
            CastException.cast(ShopCode.SHOP_MQ_MESSAGE_BODY_IS_EMPTY);
        }
        Message message = new Message(topic, tag, key, body.getBytes());
        SendResult sendResult = rocketMQTemplate.getProducer().send(message);
        return sendResult;
    }
}
