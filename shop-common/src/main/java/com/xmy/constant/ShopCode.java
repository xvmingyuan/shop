package com.xmy.constant;

public enum ShopCode {
    // 正确
    SHOP_SUCCESS(true, 1, "正确"),
    // 错误
    SHOP_FAIL(false, 0, "错误"),

    //订单服务消息状态-取消订单
    SHOP_ORDER_MESSAGE_STATUS_CANCEL(true, 1, "取消订单"),
    //订单服务消息状态-支付成功
    SHOP_ORDER_MESSAGE_STATUS_ISPAID(true, 2, "支付成功"),

    SHOP_USER_MONEY_PAID(true, 1, "付款"),
    SHOP_USER_MONEY_REFUND(true, 2, "退款"),

    SHOP_ORDER_NO_CONFIRM(false, 0, "订单未确认"),
    SHOP_ORDER_CONFIRM(true, 1, "订单已确认"),
    SHOP_ORDER_CANCEL(false, 2, "订单已取消"),
    SHOP_ORDER_INVALID(false, 3, "订单无效"),
    SHOP_ORDER_RETURNED(false, 4, "订单已退货"),
    SHOP_ORDER_FINISHED(false, 5, "订单已完成"),
    SHOP_ORDER_CALL_ERROR(false, 6, "订单异常"),

    SHOP_ORDER_PAY_STATUS_NO_PAY(true, 0, "订单未付款"),
    SHOP_ORDER_PAY_STATUS_PAYING(true, 1, "订单正在付款"),
    SHOP_ORDER_PAY_STATUS_IS_PAY(true, 2, "订单已付款"),

    SHOP_MQ_MESSAGE_STATUS_PROCESSING(true, 0, "消息正在处理"),
    SHOP_MQ_MESSAGE_STATUS_SUCCESS(true, 1, "消息处理成功"),
    SHOP_MQ_MESSAGE_STATUS_FAIL(false, 2, "消息处理失败"),

    SHOP_REQUEST_PARAMETER_VALID(false, -1, "请求参数有误"),

    SHOP_COUPON_ISUSED(true, 1, "优惠券已使用"),
    SHOP_COUPON_UNUSED(false, 0, "优惠券未使用"),

    SHOP_ORDER_STATUS_UPDATE_FAIL(false, 10001, "订单状态修改失败"),
    SHOP_ORDER_SHIPPINGFEE_INVALID(false, 10002, "订单运费不正确"),
    SHOP_ORDERMOUNT_INVALID(false, 10003, "订单总价格不正确"),
    SHOP_ORDER_SAVE_ERROR(false, 10004, "订单保存失败"),
    SHOP_ORDER_CONFIRM_FAIL(false, 10005, "订单确认失败"),

    SHOP_GOODS_NO_EXIST(false, 20001, "商品不存在"),
    SHOP_GOODS_PRICE_INVALID(false, 20002, "商品价格非法"),
    SHOP_GOODS_NUM_NOT_ENOUGH(false, 20003, "商品库存不足"),
    SHOP_REDUCE_GOODS_NUM_FAIL(false, 20004, "扣减库存失败"),
    SHOP_REDUCE_GOODS_NUM_EMPTY(false, 20005, "库存记录为空"),

    SHOP_USER_IS_NULL(false, 30001, "用户账号不能为空"),
    SHOP_USER_NO_EXIST(false, 30002, "用户不存在"),
    SHOP_USER_MONEY_REDUCE_FAIL(false, 30003, "余额扣减失败"),
    SHOP_USER_MONEY_REDUCE_ALREADY(true, 30004, "订单已退过款"),
    SHOP_USER_MONEY_REDUCE_SUCCESS(true, 30005, "余额扣减成功"),

    SHOP_COUPON_NO_EXIST(false, 40001, "优惠券不存在"),
    SHOP_COUPON_INVALIED(false, 40002, "优惠券不合法"),
    SHOP_COUPON_USE_FAIL(false, 40003, "优惠券使用失败"),
    SHOP_COUPON_USE_SUCCESS(true, 40004, "优惠券使用成功"),
    SHOP_COUPON_NO_USE(true, 40005, "未使用优惠券"),

    SHOP_MONEY_PAID_LESS_ZERO(false, 50001, "余额不能小于0"),
    SHOP_MONEY_PAID_INVALIS(false, 50002, "余额非法"),

    SHOP_MQ_TOPIC_IS_EMPTY(false, 60001, "Topic不能为空"),
    SHOP_MQ_MESSAGE_BODY_IS_EMPTY(false, 60002, "消息体不能为空"),
    SHOP_MQ_SEND_MESSAGE_FAIL(false, 60003, "消息发送失败"),

    SHOP_PAYMENT_NOT_FOUND(false, 70001, "订单未找到"),
    SHOP_PAYMENT_IS_PAID(false, 70002, "订单已支付"),
    SHOP_PAYMENT_PAY_ERROR(false, 70003, "订单支付失败"),
    SHOP_ORDER_CANCEL_CHECK(false, 70004, "订单取消审核"),
    SHOP_ORDER_CANCEL_ERROR(false, 70004, "订单取消失败"),
    SHOP_ORDER_ERROR(false, 70005, "下单失败,请联系相关人员");

    Boolean success;
    Integer code;
    String message;

    public Boolean getSuccess() {
        return success;
    }


    public Integer getCode() {
        return code;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    ShopCode() {
    }

    ShopCode(Boolean success, Integer code, String message) {
        this.success = success;
        this.code = code;
        this.message = message;
    }

    @Override
    public String toString() {
        return "ShopCode{" + "success=" + success + ", code=" + code + ", message='" + message + '\'' + '}';
    }
}
