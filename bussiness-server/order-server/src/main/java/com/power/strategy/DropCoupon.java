package com.power.strategy;

import com.power.enums.CouponEnums;
import com.power.exception.handler.BusinessException;
import com.power.pojo.Coupon;

public class DropCoupon extends Strategy {
    public DropCoupon(Coupon coupon) {
        super(coupon);
    }


    @Override
    public Double computeValue(Double primeValue) {
    if (coupon.getCouponType()!= CouponEnums.DROP_COUPON.getCode())
        throw  new BusinessException("不是立减金优惠券");
           return primeValue-coupon.getCouponPrice();
    }
}
