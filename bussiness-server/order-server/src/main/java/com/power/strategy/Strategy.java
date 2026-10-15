package com.power.strategy;

import com.power.pojo.Coupon;

public abstract class Strategy {

    public Coupon coupon;

    public Strategy(Coupon coupon) {
        this.coupon = coupon;
    }
    public abstract Double computeValue(Double primeValue);
}
