package org.springai.flightbooking.services.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.repository.AbstractRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.power.exception.handler.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springai.flightbooking.config.CancelRefuseException;
import org.springai.flightbooking.data.BookingStatus;
import org.springai.flightbooking.feign.OrderFeign;
import org.springai.flightbooking.mapper.BookingMapper;
import org.springai.flightbooking.mapper.FlightMapper;
import org.springai.flightbooking.pojo.BookingRecord;
import org.springai.flightbooking.pojo.Flight;
import org.springai.flightbooking.services.BookingService;
import org.springai.flightbooking.services.FlightService;
import org.springai.flightbooking.vo.BookingRecordVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Slf4j
public class BookingServiceImpl extends ServiceImpl<BookingMapper, BookingRecord> implements BookingService {


    @Autowired
    private  BookingMapper bookingMapper;

    @Autowired
    private FlightMapper flightMapper;

    @Autowired
    private OrderFeign orderFeign;

    @Override
    public void updateBookingRecord(Long bookingNumber, String bookingClass) {
        log.info("更新航班信息 {},{}", bookingNumber,bookingClass);
        BookingRecord record = new BookingRecord();
        record.setBookingNumber(bookingNumber);
        record.setBookingClass(bookingClass);
        bookingMapper.updateById(record);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBookingRecord(List<Long> bookingNumbers) {

        log.info("删除航班信息 {}", bookingNumbers);
        List<BookingRecord> bookingRecords = bookingMapper.selectList(new QueryWrapper<BookingRecord>()
                .in("booking_number", bookingNumbers));
        bookingRecords.forEach(record -> {
                    if (record.getDate().isAfter(LocalDateTime.now()) && Math.abs(ChronoUnit.DAYS.between(record.getDate(), LocalDateTime.now())) < 2) {
                        throw new CancelRefuseException("距离起飞时间小于48小时，不可取消航班！");
                    }
                });
        try {
            bookingMapper.update(null, new UpdateWrapper<BookingRecord>()
                    .in("booking_number", bookingNumbers)
                    .set("booking_status", BookingStatus.CANCELLED));
                     orderFeign.cancelOrder(bookingNumbers);
        } catch (Exception e) {
            log.error("取消航班失败, bookingNumbers={}", bookingNumbers, e);
            throw new BusinessException("取消航班失败,请联系管理员!");
        }


    }

    @Override
    public void insertBookingRecord(String name, String flightId, String booingClass) throws Exception {

        if (StringUtils.isEmpty(flightId)) {
            throw new BusinessException("航班编号不能为空！");
        }
        List<Flight> flights = flightMapper.selectList(new LambdaQueryWrapper<Flight>()
                .eq(Flight::getFlightId, flightId));
        
        if (flights == null || flights.isEmpty()) {
            throw new BusinessException("未找到航班编号为 " + flightId + " 的航班信息！");
        }
        Flight flight = flights.get(0);
        BookingRecord record = BookingRecord.builder()
                .flightId(flightId)
                .name(name)
                .date(flight.getDepartureTime())
                .bookingStatus(BookingStatus.CONFIRMED)
                .from(flight.getDepartureCity())
                .to(flight.getArrivalCity())
                .bookingClass(booingClass)
                .build();

        log.info("插入航班信息 {}", record);
        bookingMapper.insert(record);

    }

    @Override
    public void recoveryBookingRecord(Long bookingNumber) {
        log.info("恢复航班信息 {}", bookingNumber);
        BookingRecord record = bookingMapper.selectById(bookingNumber);
        if (record == null) {
            throw new CancelRefuseException("未找到预订号为 " + bookingNumber + " 的预订记录！");
        }
        if (record.getBookingStatus() != BookingStatus.CANCELLED) {
            throw new CancelRefuseException("该预订记录不是已取消状态，不可恢复！");
        }
        bookingMapper.update(null, new UpdateWrapper<BookingRecord>()
                .eq("booking_number", bookingNumber)
                .set("booking_status", BookingStatus.CONFIRMED));
    }

    @Override
    public List<BookingRecordVo> getBookingRecordsByPassengerIds(List<Long> passengerIds) {
        log.info("查询所有航班Vo信息 {}", passengerIds);
        List<BookingRecordVo> recordVoList = bookingMapper.getBookingRecordsByPassengerIds(passengerIds);
        return recordVoList;
    }
}