package com.xmy.pojo;

import java.io.Serializable;
import java.util.Date;

public class ShopOrderGoodsLog extends ShopOrderGoodsLogKey  implements Serializable {
    private Integer goodsNumber;

    private Date logTime;

    public Integer getGoodsNumber() {
        return goodsNumber;
    }

    public void setGoodsNumber(Integer goodsNumber) {
        this.goodsNumber = goodsNumber;
    }

    public Date getLogTime() {
        return logTime;
    }

    public void setLogTime(Date logTime) {
        this.logTime = logTime;
    }
}