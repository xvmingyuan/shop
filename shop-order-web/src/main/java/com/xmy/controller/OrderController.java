package com.xmy.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.xmy.api.IOrderService;
import com.xmy.entity.Result;
import com.xmy.pojo.ShopOrder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Reference
    private IOrderService orderService;

    @PostMapping("/confirm")
    public Result confirmOrder(@RequestBody ShopOrder order) {
        Result result = orderService.confirmOrder(order);
        return result;
    }

}
