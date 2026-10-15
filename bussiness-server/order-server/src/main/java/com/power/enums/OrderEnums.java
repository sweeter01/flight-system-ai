package com.power.enums;

/**
 * 订单状态枚举
 */
public enum OrderEnums {
    /**
     * 待支付
     */
    PENDING_PAYMENT(0, "待支付"),
    /**
     * 已支付
     */
    PAID(1, "已支付"),
    /**
     * 已取消
     */
    CANCELLED(2, "已取消"),
    /**
     * 已完成
     */
    COMPLETED(3, "已完成");


    private  Integer code;
    private  String desc;

    OrderEnums(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
