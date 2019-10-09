package com.xmy.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class ShopUserMoneyLog extends ShopUserMoneyLogKey implements Serializable {
    private Integer moneyLogType;

    private BigDecimal useMoney;

    private Date createTime;

    public Integer getMoneyLogType() {
        return moneyLogType;
    }

    public void setMoneyLogType(Integer moneyLogType) {
        this.moneyLogType = moneyLogType;
    }

    public BigDecimal getUseMoney() {
        return useMoney;
    }

    public void setUseMoney(BigDecimal useMoney) {
        this.useMoney = useMoney;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}