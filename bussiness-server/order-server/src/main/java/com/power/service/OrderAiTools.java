package com.power.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.power.enums.OrderEnums;
import com.power.pojo.Coupon;
import com.power.pojo.Order;
import com.power.pojo.Passenger;
import com.power.vo.OrderVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class OrderAiTools {

    @Autowired
    private PassengerService passengerService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CouponService couponService;

    @Tool(description = "条件查询所有乘客信息，最多返回100条记录，查询条件包括姓名、年龄、性别、身份证号、用户名")
    public List<Passenger> queryPassengerList(
            @ToolParam(description = "姓名,模糊查询，可以为空", required = false) String name,
            @ToolParam(description = "年龄下限,可以为空，必须是整数且大于0", required = false) Integer ageMin,
            @ToolParam(description = "年龄上限,可以为空，必须是整数且大于0", required = false) Integer ageMax,
            @ToolParam(description = "性别,可以为空", required = false) String sex,
            @ToolParam(description = "身份证号,可以为空", required = false) String certificate,
            @ToolParam(description = "用户名,可以为空", required = false) String username
    ) {
        log.info("AI工具-查询乘客列表: name={}, ageMin={}, ageMax={}, sex={}, certificate={}, username={}",
                name, ageMin, ageMax, sex, certificate, username);
        return passengerService.list(new LambdaQueryWrapper<Passenger>()
                .like(name != null, Passenger::getName, name)
                .between(ageMin != null && ageMax != null, Passenger::getAge, ageMin, ageMax)
                .eq(sex != null, Passenger::getSex, sex)
                .like(certificate != null, Passenger::getCertificate, certificate)
                .like(username != null, Passenger::getUsername, username)
                .last("limit 100"));
    }

    @Tool(description = "根据乘客姓名和用户名查询乘客的所有订单详情，包含航班预订信息和优惠券信息")
    public List<OrderVo> queryPassengerOrderVo(
            @ToolParam(description = "乘客姓名,必填") String passengerName,
            @ToolParam(description = "乘客用户名,必填") String username
    ) {
        log.info("AI工具-查询乘客订单详情: passengerName={}, username={}", passengerName, username);

        Long id = passengerService.getOne(new LambdaQueryWrapper<Passenger>()
                .eq(Passenger::getName, passengerName)
                .eq(Passenger::getUsername, username)).getId();
        return passengerService.getOrderVoByPassengerId(id);
    }

    @Tool(description = "查询订单列表，最多返回100条记录，根据订单编码、用户姓名、订单状态、支付方式查询")
    public List<Order> queryOrderList(
            @ToolParam(description = "订单编码,可以为空", required = false) String orderCode,
            @ToolParam(description = "用户姓名,可以为空", required = false) String passengerName,
            @ToolParam(description = "订单状态,可以为空,0-待支付,1-已支付,2-已取消,3-已完成", required = false) String status,
            @ToolParam(description = "支付方式,可以为空", required = false) String paymentMethod
    ) {
        log.info("AI工具-查询订单列表: orderCode={}, passengerName={}, status={}, paymentMethod={}",
                orderCode, passengerName, status, paymentMethod);
        return orderService.getOrderPages(orderCode, passengerName, status, paymentMethod, 1, 100).getRecords();
    }

    @Tool(description = "取消订单,根据航班预定号取消订单，将订单状态设为已取消")
    public String cancelOrder(
            @ToolParam(description = "航班预订号列表,必填") List<Long> bookingNumbers
    ) {
        log.info("AI工具-取消订单: bookingNumbers={}", bookingNumbers);
        orderService.update(new LambdaUpdateWrapper<Order>()
                .set(Order::getStatus, OrderEnums.CANCELLED)
                .in(Order::getBookingNumber, bookingNumbers));
        return "订单取消成功";
    }

    @Tool(description = "查询优惠券列表，最多返回100条记录，查询参数有:优惠券类型,优惠券状态,优惠券名称,优惠券编码")
    public List<Coupon> queryCouponList(
            @ToolParam(description = "优惠券编码,不传递则为null", required = false) String couponCode,
            @ToolParam(description = "优惠券类型,不传递则为null,1-无门槛券,2-满减券,3-折扣券", required = false) Integer couponType,
            @ToolParam(description = "优惠券名称,不传递则为null", required = false) String couponName,
            @ToolParam(description = "优惠券状态,不传递则为null,0-可用,1-不可用", required = false) Integer status
    ) {
        log.info("AI工具-查询优惠券列表: couponCode={}, couponType={}, couponName={}, status={}",
                couponCode, couponType, couponName, status);
        return couponService.list(new LambdaQueryWrapper<Coupon>()
                .eq(couponCode != null, Coupon::getCouponCode, couponCode)
                .eq(couponType != null, Coupon::getCouponType, couponType)
                .eq(status != null, Coupon::getStatus, status)
                .like(couponName != null, Coupon::getCouponName, couponName)
                .last("limit 100"));
    }
}