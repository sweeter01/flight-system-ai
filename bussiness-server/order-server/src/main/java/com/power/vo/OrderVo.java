package com.power.vo;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.power.pojo.Coupon;
import com.power.pojo.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springai.flightbooking.pojo.BookingRecord;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderVo extends Order {

    @JsonIgnore
    private  Long bookingNumber;
    private  Double  primePrice;
    private  Double  totalPrice;
    private BookingRecord bookingRecord; // 航班记录信息实体类
    private Coupon coupon;  // 优惠券信息实体类
}
