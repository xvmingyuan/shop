package com.xmy.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.xmy.api.IOrderService;
import com.xmy.entity.Result;
import com.xmy.pojo.ShopOrder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Reference
    private IOrderService orderService;

    @PostMapping("/confirm")
    public Result confirmOrder(@RequestBody ShopOrder order) {
        ShopOrder shopOrder = new ShopOrder();
        shopOrder.setGoodsId(order.getGoodsId());
        shopOrder.setUserId(order.getUserId());
        shopOrder.setCouponId(order.getCouponId());
        shopOrder.setAddress("北京");
        shopOrder.setGoodsNumber(1);
        shopOrder.setGoodsPrice(new BigDecimal(1000));
        shopOrder.setGoodsAmount(new BigDecimal(1000));
        shopOrder.setShippingFee(BigDecimal.ZERO);
        shopOrder.setOrderAmount(new BigDecimal(1000));
        shopOrder.setMoneyPaid(new BigDecimal(100));
        Result result = orderService.confirmOrder(shopOrder);
        return result;
    }

}
