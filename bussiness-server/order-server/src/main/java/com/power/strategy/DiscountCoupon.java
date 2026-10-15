package com.power.strategy;

import com.power.enums.CouponEnums;
import com.power.exception.handler.BusinessException;
import com.power.pojo.Coupon;

public class DiscountCoupon extends Strategy {


    public DiscountCoupon(Coupon coupon) {
        super(coupon);
    }

    @Override
    public Double computeValue(Double primeValue) {
        if (coupon.getCouponType()!= CouponEnums.DISCOUNT_COUPON.getCode())
            throw  new BusinessException("不是折扣优惠券");
        return primeValue * coupon.getCouponPrice() / 100.0;
    }
}
