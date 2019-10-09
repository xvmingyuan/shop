package com.xmy.exception;

import com.xmy.constant.ShopCode;

public class CustomerException extends RuntimeException {

    private ShopCode shopCode;

    public CustomerException(ShopCode shopCode) {
        this.shopCode = shopCode;

    }
}
