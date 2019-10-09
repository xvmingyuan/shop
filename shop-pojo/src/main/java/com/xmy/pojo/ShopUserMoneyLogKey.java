package com.xmy.pojo;

import java.io.Serializable;

public class ShopUserMoneyLogKey implements Serializable {
    private Long userId;

    private Long orderId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
}