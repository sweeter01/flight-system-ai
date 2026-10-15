package com.power.strategy;

import com.power.enums.CouponEnums;
import com.power.pojo.Coupon;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestUse {
    @Test
    void testTopDropCoupon() {
        Coupon coupon = new Coupon();
        coupon.setCouponName("满100减30优惠券");
        coupon.setCouponType(CouponEnums.TOP_DROP_COUPON.getCode());
        coupon.setCouponPrice(30.0);

        CouponStrategy couponStrategy = new CouponStrategy(coupon);
        double realPrice = couponStrategy.getRealPrice(110.0);

        System.out.println(realPrice);


    }
}
