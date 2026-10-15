package com.power.fegin;

import com.power.fegin.impl.RecordFeignImpl;
import org.springai.flightbooking.pojo.BookingRecord;
import org.springai.flightbooking.vo.BookingRecordVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "flight-booking",fallback = RecordFeignImpl.class)
public interface RecordFeign {

    @GetMapping("/api/record")
    public List<BookingRecordVo> getBookingRecordsByPassengerIds(@RequestParam("passengerIds") List<Long> passengerIds);

    @GetMapping("/flightPrice/orderPrice")
    public List<Double> getOrderPrices(@RequestParam("flightIds") List<String> flightIds, @RequestParam("seatTypes") List<String> seatTypes);

}