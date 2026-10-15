package com.power.controller;


import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.power.enums.OrderEnums;
import com.power.enums.PayEnums;
import com.power.model.Result;
import com.power.pojo.Order;
import com.power.service.OrderService;
import com.power.vo.OrderVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/order")
@Slf4j
@Api(tags = "订单信息管理")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/list")
    @ApiOperation(value = "查询订单列表")
    @PreAuthorize("hasAuthority('order:list')")
    public Result<Page<Order>> getOrderPages(
            String orderCode,
            String passengerName,
            String status,
            String paymentMethod,
            @RequestParam(value = "current",defaultValue = "1") Integer current,
            @RequestParam(value = "size",defaultValue = "10") Integer size
    ) {

       log.info("查询订单列表,订单编码:{},用户姓名:{},订单状态:{},支付方式:{}",
               orderCode,passengerName,status,paymentMethod);
       return Result.success(orderService.getOrderPages(orderCode,passengerName,status,paymentMethod,current,size));
    }

    @GetMapping("/order/detail")
    @ApiOperation("根据订单id查询订单详情")
    public Result<OrderVo> getOrderVoByOrderID(Long id) {
        log.info("根据订单id查询订单详情,订单id:{}",id);
        return Result.success(orderService.getOrderVoByOrderID(id));
    }

    @PutMapping("/update")
    @ApiOperation(value = "更新订单信息")
    @PreAuthorize("hasAuthority('order:update')")
    public Result updateOrder(@RequestBody Order order) {
        log.info("更新订单信息,订单编码:{}",order.getOrderCode());
        order.setUpdateTime(LocalDateTime.now());
        orderService.updateById(order);
        return Result.success();
    }

    @DeleteMapping("/delete/{ids}")
    @ApiOperation(value = "批量删除订单")
    @PreAuthorize("hasAuthority('order:delete')")
    public Result deleteOrder(@PathVariable("ids") List<Long> ids) {
        log.info("批量删除订单,订单id:{}",ids);
        orderService.removeOrderByIds(ids);
        return Result.success();
    }

    /// //////////// feign //////////////

    @DeleteMapping("/cancelOrder")
    @ApiOperation(value = "取消订单")
    @PreAuthorize("hasAuthority('order:cancel')")
    public Result cancelOrder(@RequestParam("bookingNumbers") List<Long> bookingNumbers) {

        log.info("取消订单,订单id:{}",bookingNumbers);
        orderService.update(new LambdaUpdateWrapper<Order>()
                .set(Order::getStatus, OrderEnums.CANCELLED)
                .in(Order::getBookingNumber,bookingNumbers));
        return Result.success();
    }


}