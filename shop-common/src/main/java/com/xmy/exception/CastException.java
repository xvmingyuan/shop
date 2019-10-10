package com.xmy.exception;

import com.xmy.constant.ShopCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CastException {
    public static void cast(ShopCode shopCode) throws CustomerException {
        log.error(shopCode.toString());
        throw new CustomerException(shopCode);
    }
}
