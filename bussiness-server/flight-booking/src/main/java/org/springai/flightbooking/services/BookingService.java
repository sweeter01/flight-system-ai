package org.springai.flightbooking.services;


import com.baomidou.mybatisplus.extension.service.IService;
import org.springai.flightbooking.pojo.BookingRecord;
import org.springai.flightbooking.vo.BookingRecordVo;

import java.util.List;

public interface BookingService extends IService<BookingRecord> {


    void updateBookingRecord(Long bookingNumber,String bookingClass);

    void deleteBookingRecord(List<Long> bookingNumbers);

    void insertBookingRecord(String name,String flightId,String booingClass) throws Exception;


    void  recoveryBookingRecord(Long bookingNumber);

    List<BookingRecordVo> getBookingRecordsByPassengerIds(List<Long> passengerIds);

}
