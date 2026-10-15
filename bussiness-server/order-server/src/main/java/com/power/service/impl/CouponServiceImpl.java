package com.power.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.power.mapper.CouponMapper;
import com.power.pojo.Coupon;
import com.power.service.CouponService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class CouponServiceImpl extends ServiceImpl<CouponMapper, Coupon> implements CouponService {
}
