package com.power.enums;

public enum CouponEnums {

     /**
     * 立减金优惠券
     * */
   DROP_COUPON(1,"无门槛券"),
    /**
     * 满减优惠券
     */
    TOP_DROP_COUPON(2,"满减优惠券"),

    /**
     * 折扣券
     */
    DISCOUNT_COUPON(3,"折扣券");

    private Integer code;
    private String name;

    CouponEnums(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
