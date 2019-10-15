package com.xmy.pojo;

public class ShopOrderMqStatusLog {
    private Long orderId;

    private Integer goodsStatus;

    private String goodsResult;

    private Integer couponStatus;

    private String couponResult;

    private Integer userMoneyStatus;

    private String userResult;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Integer getGoodsStatus() {
        return goodsStatus;
    }

    public void setGoodsStatus(Integer goodsStatus) {
        this.goodsStatus = goodsStatus;
    }

    public String getGoodsResult() {
        return goodsResult;
    }

    public void setGoodsResult(String goodsResult) {
        this.goodsResult = goodsResult == null ? null : goodsResult.trim();
    }

    public Integer getCouponStatus() {
        return couponStatus;
    }

    public void setCouponStatus(Integer couponStatus) {
        this.couponStatus = couponStatus;
    }

    public String getCouponResult() {
        return couponResult;
    }

    public void setCouponResult(String couponResult) {
        this.couponResult = couponResult == null ? null : couponResult.trim();
    }

    public Integer getUserMoneyStatus() {
        return userMoneyStatus;
    }

    public void setUserMoneyStatus(Integer userMoneyStatus) {
        this.userMoneyStatus = userMoneyStatus;
    }

    public String getUserResult() {
        return userResult;
    }

    public void setUserResult(String userResult) {
        this.userResult = userResult == null ? null : userResult.trim();
    }
}