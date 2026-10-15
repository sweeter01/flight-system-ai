package com.power.fegin.impl;

import com.power.fegin.RecordFeign;
import lombok.extern.slf4j.Slf4j;
import org.springai.flightbooking.vo.BookingRecordVo;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@Slf4j
public class RecordFeignImpl implements RecordFeign {

    @Override
    public List<BookingRecordVo> getBookingRecordsByPassengerIds(List<Long> passengerIds) {
        log.info("远程调用查询乘客航班记录失败！乘客ID列表：{}", passengerIds);
        return List.of();
    }

    @Override
    public List<Double> getOrderPrices(List<String> flightIds, List<String> seatTypes) {
        log.info("远程调用查询订单价格失败！航班ID列表：{}，座位类型列表：{}", flightIds, seatTypes);
        return List.of();
    }


}
