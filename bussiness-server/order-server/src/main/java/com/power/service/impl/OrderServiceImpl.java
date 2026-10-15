package com.power.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.power.enums.OrderEnums;
import com.power.enums.PayEnums;
import com.power.fegin.RecordFeign;
import com.power.mapper.CouponMapper;
import com.power.mapper.OrderMapper;
import com.power.pojo.Coupon;
import com.power.pojo.Order;
import com.power.service.OrderService;
import com.power.strategy.CouponStrategy;
import com.power.vo.OrderVo;
import lombok.extern.slf4j.Slf4j;
import org.springai.flightbooking.vo.BookingRecordVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private  OrderMapper orderMapper;

    @Autowired
    private CouponMapper couponMapper;

    @Autowired
    private RecordFeign recordFeign;
    @Override
    public Page<Order> getOrderPages(String orderCode, String passengerName, String status, String paymentMethod, Integer current, Integer size) {

        log.info("查询订单列表,订单编码:{},用户姓名:{},订单状态:{},支付方式:{}",
                orderCode,passengerName,status,paymentMethod);
                  Page<Order> page = new Page<>(current, size);
          page=orderMapper.selectPage(page,new LambdaQueryWrapper<Order>()
                .eq(orderCode != null, Order::getOrderCode, orderCode)
                .like(passengerName != null, Order::getPassengerName, passengerName)
                .eq(status != null, Order::getStatus, status)
                .eq(paymentMethod != null, Order::getPaymentMethod, paymentMethod)
        );
          return page;
    }

    @Override
    public OrderVo getOrderVoByOrderID(Long id) {

        log.info("根据订单ID查询订单的详情,订单ID: {}",id);
        Long passengerId = orderMapper.selectOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getId, id)).getPassengerId();
        List<OrderVo> orderVos = getPassengerOrderVo(List.of(passengerId));
        return orderVos.get(0);
    }

    @Override
    public void removeOrderByIds(List<Long> ids) {
        List<Integer> statusList = orderMapper.selectByIds(ids).stream().map(Order::getStatus).toList();
        if (statusList.stream().anyMatch(status -> status != OrderEnums.COMPLETED.getCode()
                && status != OrderEnums.CANCELLED.getCode())) {
            throw new IllegalArgumentException("只能删除已完成或已取消订单!");
        }
        orderMapper.deleteByIds(ids);

    }


    private Double useCouponStrategy(Coupon coupon, Double primePrice) {
        if (coupon==null)
            return primePrice;
        CouponStrategy strategy = new CouponStrategy(coupon);
        return strategy.getRealPrice(primePrice);
    }

    /*
     * @param passengerId 乘客ID
     * @return 乘客的所有订单詳情列表【包含优惠券信息和订单金额】
     * */
    public List<OrderVo> getPassengerOrderVo(List<Long> passengerIds) {
        List<Order> orders = orderMapper.selectList(new LambdaQueryWrapper<Order>()
                .in(Order::getPassengerId, passengerIds));
        if (orders.isEmpty()) return new ArrayList<>();

        // 1. 用 HashMap 索引预订记录，O(1) 查找替代 O(m) 遍历
        List<BookingRecordVo> personalRecords = recordFeign
                .getBookingRecordsByPassengerIds(passengerIds);
        Map<Long, BookingRecordVo> recordMap = personalRecords.stream()
                .collect(Collectors.toMap(BookingRecordVo::getBookingNumber, r -> r, (a, b) -> a));

        // 2. 用 HashMap 索引优惠券，O(1) 查找替代 O(c) 遍历
        List<String> couponCodes = orders.stream()
                .map(Order::getCouponCode)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        Map<String, Coupon> couponMap = couponCodes.isEmpty() ? Collections.emptyMap() :
                couponMapper.selectList(new LambdaQueryWrapper<Coupon>()
                                .in(Coupon::getCouponCode, couponCodes))
                        .stream().collect(Collectors.toMap(Coupon::getCouponCode, c -> c, (a, b) -> a));
        // 3. 组装 OrderVo
        List<OrderVo> orderVos = new ArrayList<>();
        for (Order order : orders) {
            OrderVo orderVo = new OrderVo();
            BeanUtils.copyProperties(order, orderVo);

            BookingRecordVo record = recordMap.get(order.getBookingNumber());
            orderVo.setBookingRecord(record);
            if (record != null) {
                orderVo.setPrimePrice(record.getPrice());
            }

            PayEnums payEnum = PayEnums.fromValue(order.getPaymentMethod());
            if (payEnum != null) {
                orderVo.setPaymentMethod(payEnum.name());
            }

            Coupon coupon = couponMap.get(order.getCouponCode());
            orderVo.setCoupon(coupon);

            if (orderVo.getPrimePrice() != null) {
                orderVo.setTotalPrice(
                        useCouponStrategy(coupon, orderVo.getPrimePrice()) * order.getDiscount());
            }

            orderVos.add(orderVo);
        }
        return orderVos;
    }

}