package com.power.strategy;

import com.power.enums.CouponEnums;
import com.power.exception.handler.BusinessException;
import com.power.pojo.Coupon;

public class TopDropCoupon extends Strategy {


    public TopDropCoupon(Coupon coupon) {
        super(coupon);
    }

    @Override
    public Double computeValue(Double primeValue) {
        if (coupon.getCouponType()!= CouponEnums.TOP_DROP_COUPON.getCode()) {
            throw  new BusinessException("不是满减优惠券");
        }
        String substring = coupon.getCouponName().substring(1, coupon.getCouponName().indexOf("减"));
        Double  threshold = 0.0;
        try {
            threshold = Double.valueOf(substring);
        } catch (Exception e) {
            throw new BusinessException("满减优惠券阈值格式错误");
        }
        return  threshold <= primeValue ? primeValue-coupon.getCouponPrice() : primeValue;
    }
}
