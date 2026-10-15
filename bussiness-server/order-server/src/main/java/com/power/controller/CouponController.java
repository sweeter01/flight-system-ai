package com.power.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.power.model.Result;
import com.power.pojo.Coupon;
import com.power.service.CouponService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Api("优惠券接口")
@RequestMapping("/coupon")
@Slf4j
public class CouponController {


    @Autowired
    private CouponService couponService;
    @GetMapping("/list")
    @ApiOperation(value = "查询优惠券列表")
    @PreAuthorize("hasAuthority('coupon:list')")
    public Result<List<Coupon>> getCouponVoList(
            String couponCode,
            Integer couponType,
            String couponName,
            Integer status

    ) {
        log.info("查询优惠券列表,优惠券编码: {},优惠券类型: {},优惠券名称: {},优惠券状态: {}"
                ,couponCode,couponType,couponName,status);
        List<Coupon> coupons = couponService.list(new LambdaQueryWrapper<Coupon>()
                .eq(couponCode!=null,Coupon::getCouponCode, couponCode)
                .eq(couponType!=null,Coupon::getCouponType, couponType)
                .eq(status!=null,Coupon::getStatus, status)
                .like(couponName!=null,Coupon::getCouponName, couponName));

        return Result.success(coupons);
    }

    @PutMapping("/update")
    @ApiOperation(value = "更新优惠券")
    @PreAuthorize("hasAuthority('coupon:update')")
    public Result updateCoupon(@RequestBody Coupon coupon) {
        log.info("更新优惠券,优惠券: {}",coupon);
        coupon.setUpdateTime(LocalDateTime.now());
        couponService.updateById(coupon);
        return Result.success();
    }

    @PostMapping("/add")
    @ApiOperation(value = "添加优惠券")
    @PreAuthorize("hasAuthority('coupon:add')")
    public Result addCoupon(@RequestBody Coupon coupon) {
        log.info("添加优惠券,优惠券: {}",coupon);
        coupon.setCreateTime(LocalDateTime.now());
        coupon.setUpdateTime(LocalDateTime.now());
        couponService.save(coupon);
        return Result.success();
    }
    @DeleteMapping("/delete/{ids}")
    @ApiOperation(value = "批量删除优惠券")
    @PreAuthorize("hasAuthority('coupon:delete')")
    public Result deleteCoupon(@PathVariable("ids") List<Long> ids) {
        log.info("批量删除优惠券,优惠券id: {}",ids);
        couponService.removeByIds(ids);
        return Result.success();
    }

}