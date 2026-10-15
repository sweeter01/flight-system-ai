package org.springai.flightbooking.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import jdk.dynalink.linker.LinkerServices;
import org.apache.ibatis.annotations.Mapper;
import org.springai.flightbooking.pojo.BookingRecord;
import org.springai.flightbooking.vo.BookingRecordVo;

import java.util.List;

public interface BookingMapper extends BaseMapper<BookingRecord> {

    List<BookingRecordVo> getBookingRecordsByPassengerIds(List<Long> passengerIds);

}
