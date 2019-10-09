package com.xmy.api;

import com.xmy.entity.Result;
import com.xmy.pojo.ShopPay;

public interface IPayService {
    /**
     * 创建支付单据接口
     *
     * @param shopPay
     * @return
     */
    Result createPayment(ShopPay shopPay);

    /**
     * 支付回调接口
     *
     * @param shopPay
     * @return
     */
    Result callbackPayment(ShopPay shopPay);
}
