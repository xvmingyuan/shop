package com.xmy.test;

import com.xmy.PayWebApplication;
import com.xmy.constant.ShopCode;
import com.xmy.entity.Result;
import com.xmy.pojo.ShopPay;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;

@SuppressWarnings("ALL")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PayWebApplication.class)
public class PayWebTest {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${shop.pay.baseURI}")
    private String baseURI;

    @Value("${shop.pay.createPayment}")
    private String createPayment;

    @Value("${shop.pay.callbackPayment}")
    private String callbackPayment;
    @Test
    public void createPayment() throws IOException {
        Long orderId = 379321587406606336L;

        ShopPay pay = new ShopPay();
        pay.setOrderId(orderId);
        pay.setIsPaid(ShopCode.SHOP_ORDER_PAY_STATUS_NO_PAY.getCode());
        pay.setPayAmount(new BigDecimal(600));
        Result result = restTemplate.postForObject(baseURI + createPayment, pay, Result.class);
        System.out.println(result);
        System.in.read();

    }

    @Test
    public void callbackPayment() throws IOException {
        Long orderId = 379321587406606336L;
        Long payId = 379265591703379968L;

        ShopPay pay = new ShopPay();
        pay.setPayId(payId);
        pay.setOrderId(orderId);
        pay.setIsPaid(ShopCode.SHOP_ORDER_PAY_STATUS_IS_PAY.getCode());
        Result result = restTemplate.postForObject(baseURI + callbackPayment, pay, Result.class);
        System.out.println(result);

    }
}
