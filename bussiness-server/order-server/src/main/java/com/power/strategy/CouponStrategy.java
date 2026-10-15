package com.power.strategy;

import com.power.exception.handler.BusinessException;
import com.power.pojo.Coupon;

public class CouponStrategy {

    public  Strategy strategy;
    private  Coupon coupon;

    public  CouponStrategy(Coupon coupon) {
        this.coupon = coupon;
        switch (coupon.getCouponType()) {
            case 1:
                strategy = new DropCoupon(coupon);
                break;
            case 2:
                strategy = new TopDropCoupon(coupon);
                break;
            case 3:
                strategy = new DiscountCoupon(coupon);
                break;
            default:
                throw new BusinessException("优惠券类型错误");
        }
    }


    public  double getRealPrice(Double primePrice){
        return  strategy.computeValue(primePrice);
    }

}
