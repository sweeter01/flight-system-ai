package org.springai.flightbooking.feign.impl;

import lombok.extern.slf4j.Slf4j;
import org.springai.flightbooking.feign.OrderFeign;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class OrderFeignImpl implements OrderFeign {
    @Override
    public void cancelOrder(List<Long> bookingNumbers) {
        log.info("取消订单,航班预定号:{}",bookingNumbers);
    }
}
