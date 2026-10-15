package org.springai.flightbooking.client;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.power.model.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springai.flightbooking.pojo.BookingRecord;
import org.springai.flightbooking.services.BookingService;
import org.springai.flightbooking.vo.BookingRecordVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;


@Api("客户航班信息")
@RestController
@CrossOrigin
@Slf4j
public class BookingController {

	@Autowired
	private BookingService bookingService;

	@ApiOperation("查询客户航班信息")
	@GetMapping("/api/bookings")
	public Result<Page<BookingRecord>> getBookingsDB(
			String name,
			String flightId,
			LocalDateTime date,
			String bookingStatus,
			String bookingClass,
			Long current,Long size)
	{

		log.info("------------查询航班信息------------: {},{},{},{},{},{},{},{}", current,size,name,flightId, date, bookingStatus, bookingClass);
		Page<BookingRecord> bookingRecordPage = new Page<>(current == null ? 1 : current, size == null ? 10 : size);
		Page<BookingRecord> page = bookingService.page(bookingRecordPage, new QueryWrapper<BookingRecord>()
				.like(name != null && !name.isEmpty(), "name", name)
				.eq(flightId != null && !flightId.isEmpty(), "flight_id", flightId)
//				.like(from != null && !from.isEmpty(), "`from`", from)
//				.like(to != null && !to.isEmpty(), "`to`", to)
				.eq(date != null && !date.toString().isEmpty(), "date", date)
				.eq(bookingStatus != null && !bookingStatus.isEmpty(), "booking_status", bookingStatus)
				.eq(bookingClass != null && !bookingClass.isEmpty(), "booking_class", bookingClass)
		);
		return Result.success(page);
	}

	@ApiOperation("修改客户航班信息")
	@PutMapping("/api/changeBooking")
	public Result updateBookingRecord(Long bookingNumber, String bookingClass) {

		bookingService.updateBookingRecord(bookingNumber, bookingClass);
		return Result.success();
	}


	@ApiOperation("取消客户航班信息")
	@DeleteMapping("/api/cancelBooking/{ids}")
	public Result  cancelBooking(@PathVariable("ids") List<Long> bookingNumbers) {
		log.info("------------取消航班信息------------: {}", bookingNumbers);
			bookingService.deleteBookingRecord(bookingNumbers);
			return Result.success();

	}
	@ApiOperation("添加客户航班信息")
	@PostMapping
	public Result addBookingRecord(String name, String flightId, String bookingClass) throws Exception {
		log.info("------------添加航班信息------------: {}，{}", name, bookingClass);
		bookingService.insertBookingRecord(name,flightId,bookingClass);
		return Result.success();
	}

    @ApiOperation("恢复客户航班信息")
    @PutMapping("/api/recoveryBooking")
	public Result recoveryBookingRecord(Long bookingNumber) {

		log.info("------------恢复航班信息------------: {}", bookingNumber);
			bookingService.recoveryBookingRecord(bookingNumber);
		return Result.success();

	}
	/// //////////////  feign调用 ///////////////

	@GetMapping("/api/record")
	public List<BookingRecordVo> getBookingRecordsByPassengerIds(@RequestParam("passengerIds") List<Long> passengerIds) {

		log.info("------------查询所有航班信息------------: {}", passengerIds);
		return bookingService.getBookingRecordsByPassengerIds(passengerIds);


	}

}