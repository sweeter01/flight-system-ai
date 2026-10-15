package org.springai.flightbooking.services;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springai.flightbooking.pojo.BookingRecord;
import org.springai.flightbooking.pojo.Flight;
import org.springai.flightbooking.pojo.FlightPrice;
import org.springai.flightbooking.pojo.Plane;
import org.springai.flightbooking.services.BookingService;
import org.springai.flightbooking.services.FlightPriceService;
import org.springai.flightbooking.services.FlightService;
import org.springai.flightbooking.services.PlaneService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class FlightBookingAiTools {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private FlightService flightService;

    @Autowired
    private PlaneService planeService;

    @Autowired
    private FlightPriceService flightPriceService;

    @Tool(description = "根据预定号、客户姓名、出发地、目的地、日期、状态或舱位等级查询航班预定信息，最多返回100条记录")
    public List<BookingRecord> queryBookings(
            @ToolParam(description = "预定号，可为空", required = false) String bookingNumber,
            @ToolParam(description = "客户姓名，可为空", required = false) String name,
            @ToolParam(description = "航班批次编号", required = false) String flightId,
            @ToolParam(description = "出发地，可为空", required = false) String from,
            @ToolParam(description = "目的地，可为空", required = false) String to,
            @ToolParam(description = "出发时间，可为空", required = false) LocalDateTime date,
            @ToolParam(description = "状态，可选值：CONFIRMED/CANCELLED/COMPLETED，可为空", required = false) String bookingStatus,
            @ToolParam(description = "舱位等级，可选值：ECONOMY/PREMIUM_ECONOMY/BUSINESS/FIRST，可为空", required = false) String bookingClass) {
        log.info("AI工具-查询航班预定: bookingNumber={}, name={}, flightId={}, from={}, to={}, date={}, status={}, class={}",
                bookingNumber, name, flightId, from, to, date, bookingStatus, bookingClass);
        return bookingService.list(new QueryWrapper<BookingRecord>()
                .eq(bookingNumber != null && !bookingNumber.isEmpty(), "booking_number", bookingNumber)
                .like(name != null && !name.isEmpty(), "name", name)
                .eq(flightId != null && !flightId.isEmpty(), "flight_id", flightId)
                .like(from != null && !from.isEmpty(), "`from`", from)
                .like(to != null && !to.isEmpty(), "`to`", to)
                .eq(date != null && !date.toString().isEmpty(), "date", date)
                .eq(bookingStatus != null && !bookingStatus.isEmpty(), "booking_status", bookingStatus)
                .eq(bookingClass != null && !bookingClass.isEmpty(), "booking_class", bookingClass)
                .last("limit 100"));
    }

    @Tool(description = "修改航班预定信息，只能修改舱位类型")
    public String updateBookingRecord(
            @ToolParam(description = "预订号") Long bookingNumber,
            @ToolParam(description = "新舱位等级，可选值：ECONOMY(经济舱)/PREMIUM_ECONOMY(超级经济舱)/BUSINESS(商务舱)/FIRST(头等舱)，如果不修改则传null") String bookingClass) {
        log.info("AI工具-修改航班预定: bookingNumber={}, bookingClass={}", bookingNumber, bookingClass);
        bookingService.updateBookingRecord(bookingNumber, bookingClass);
        return "航班预定修改成功";
    }

    @Tool(description = "取消航班预定，当用户要求取消、退票时使用此工具")
    public String cancelBooking(
            @ToolParam(description = "预订号集合,不为空") List<Long> bookingNumbers) {
        log.info("AI工具-取消航班预定: bookingNumbers={}", bookingNumbers);
        bookingService.deleteBookingRecord(bookingNumbers);
        return "航班预定取消成功";
    }

    @Tool(description = "添加航班预定信息，当用户要求添加、预订、订票时使用此工具")
    public String addBookingRecord(
            @ToolParam(description = "客户姓名") String name,
            @ToolParam(description = "航班批次编号") String flightId,
            @ToolParam(description = "舱位等级，可选值：ECONOMY(经济舱)/PREMIUM_ECONOMY(超级经济舱)/BUSINESS(商务舱)/FIRST(头等舱)") String bookingClass) throws Exception {
        log.info("AI工具-添加航班预定: name={}, flightId={}, bookingClass={}", name, flightId, bookingClass);
        bookingService.insertBookingRecord(name, flightId, bookingClass);
        return "航班预定添加成功";
    }

    @Tool(description = "恢复航班预定信息，当用户要求恢复已取消的预定时使用此工具")
    public String recoveryBookingRecord(
            @ToolParam(description = "预订号,不为空") Long bookingNumber) {
        log.info("AI工具-恢复航班预定: bookingNumber={}", bookingNumber);
        bookingService.recoveryBookingRecord(bookingNumber);
        return "航班预定恢复成功";
    }

    @Tool(description = "查询具体航班信息，最多返回100条记录")
    public List<Flight> queryFlightList(
            @ToolParam(description = "航班编号,可为空", required = false) String flightId,
            @ToolParam(description = "客机类型,可为空", required = false) String flightType,
            @ToolParam(description = "出发时间,可为空", required = false) LocalDateTime departureTime,
            @ToolParam(description = "出发城市,可为空", required = false) String departureCity,
            @ToolParam(description = "到达城市,可为空", required = false) String arrivalCity) {
        log.info("AI工具-查询航班信息: flightId={}, flightType={}, departureTime={}, departureCity={}, arrivalCity={}", flightId, flightType, departureTime, departureCity, arrivalCity);
        return flightService.list(new LambdaQueryWrapper<Flight>()
                .eq(StringUtils.hasText(flightId), Flight::getFlightId, flightId)
                .eq(StringUtils.hasText(flightType), Flight::getPlaneType, flightType)
                .eq(departureTime != null, Flight::getDepartureTime, departureTime)
                .eq(StringUtils.hasText(departureCity), Flight::getDepartureCity, departureCity)
                .eq(StringUtils.hasText(arrivalCity), Flight::getArrivalCity, arrivalCity)
                .last("limit 100"));
    }

    @Tool(description = "查询航空公司的客机信息，最多返回100条记录")
    public List<Plane> queryPlaneList(
            @ToolParam(description = "客机编号", required = false) Long id,
            @ToolParam(description = "客机类型", required = false) String planeType,
            @ToolParam(description = "状态", required = false) Integer status) {
        log.info("AI工具-查询客机信息: id={}, planeType={}", id, planeType);
        return planeService.list(new LambdaQueryWrapper<Plane>()
                .eq(id != null && id > 0, Plane::getId, id)
                .like(planeType != null && !planeType.isEmpty(), Plane::getPlaneType, planeType)
                .eq(status != null, Plane::getStatus, status)
                .last("limit 100"));
    }

    @Tool(description = "查询航班价格信息，最多返回100条记录，可根据航班编号、客舱类型、价格范围、状态筛选")
    public List<FlightPrice> queryFlightPriceList(
            @ToolParam(description = "航班批次编号,可为空", required = false) Long flightId,
            @ToolParam(description = "客舱类型,可为空,如ECONOMY/BUSINESS/FIRST", required = false) String seatType,
            @ToolParam(description = "最低价格,可为空", required = false) Double lowPrice,
            @ToolParam(description = "最高价格,可为空", required = false) Double highPrice,
            @ToolParam(description = "状态,可为空,1可用/0不可用", required = false) Integer status) {
        log.info("AI工具-查询航班价格: flightId={}, seatType={}, lowPrice={}, highPrice={}, status={}", flightId, seatType, lowPrice, highPrice, status);
        return flightPriceService.list(new LambdaQueryWrapper<FlightPrice>()
                .eq(flightId != null, FlightPrice::getFlightId, flightId)
                .eq(seatType != null, FlightPrice::getSeatType, seatType)
                .eq(status != null, FlightPrice::getStatus, status)
                .between(lowPrice != null && highPrice != null, FlightPrice::getPrice, lowPrice, highPrice)
                .last("limit 100"));
    }
}