package org.springai.flightbooking.feign;

import org.springai.flightbooking.feign.impl.OrderFeignImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "order-server",fallback = OrderFeignImpl.class)
public interface OrderFeign {


    @DeleteMapping("/order/cancelOrder")
    void  cancelOrder(@RequestParam("bookingNumbers") List<Long> bookingNumbers);
}
