package org.springai.flightbooking.client;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.power.model.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springai.flightbooking.pojo.Flight;
import org.springai.flightbooking.services.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Api(tags = "具体航班信息")
@RestController
@Slf4j
@CrossOrigin
@RequestMapping("/flight")
public class FlightController {

    @Autowired
    private FlightService flightService;

    @ApiOperation(value = "查询具体航班信息", notes = "根据航班编号查询具体航班信息")
    @GetMapping("/getFlight")
    @PreAuthorize("hasAuthority('flight:getFlight')")
    public Result<Page<Flight>> getFlightByInfo(
            String flightId,
            String flightType,
            LocalDateTime departureTime,
            String departureCity,
            String arrivalCity,
           Long current, Long size
            ){

        Page<Flight> flightPage = new Page<>(current == null ? 1 : current, size == null ? 10 : size);
        flightPage= flightService.page(flightPage, new LambdaQueryWrapper<Flight>()
                .eq(StringUtils.hasText(flightId), Flight::getFlightId, flightId)
                .eq(StringUtils.hasText(flightType), Flight::getPlaneType, flightType)
                .eq(departureTime != null, Flight::getDepartureTime, departureTime)
                .eq(StringUtils.hasText(departureCity), Flight::getDepartureCity, departureCity)
                .eq(StringUtils.hasText(arrivalCity), Flight::getArrivalCity, arrivalCity));

        log.info("records: {}", flightPage.getRecords().toString());
        return Result.success(flightPage);
    }


    @ApiOperation(value = "更新具体航班信息", notes = "根据航班编号更新具体航班信息")
    @PutMapping("/updateFlight")
    @PreAuthorize("hasAuthority('flight:updateFlight')")
    public Result<String> updateFlight(Flight flight) {
        log.info("更新航班信息: {}", flight.toString());
        flightService.updateFlight(flight);
        return Result.success("更新成功");
    }

    @ApiOperation(value = "删除具体航班信息", notes = "根据航班编号删除具体航班信息")
    @DeleteMapping("/deleteFlight")
    @PreAuthorize("hasAuthority('flight:deleteFlight')")
    public Result<String> deleteFlightById(Long id) {
        log.info("删除航班信息: {}", id);
        flightService.deleteFlightById(id);
        return Result.success("删除成功");
    }
    @ApiOperation(value = "添加具体航班信息", notes = "添加具体航班信息")
    @PostMapping("/addFlight")
    @PreAuthorize("hasAuthority('flight:addFlight')")
    public Result<String> addFlight(Long planeId,
                                     LocalDateTime departureTime,
                                     LocalDateTime arrivalTime,
                                     String departureCity,
                                     String arrivalCity

    ) {

        log.info("添加航班信息: {}", planeId);
        flightService.addFlight(planeId, departureTime, arrivalTime, departureCity, arrivalCity);
        return Result.success("添加成功");
    }
    /// ///////   feign调用接口 /////////////

    @ApiOperation(value = "批量查询航班出发到达时间", notes = "根据航班编号列表查询航班出发到达时间")
    @GetMapping("/getFlyingTime/{flightIdList}")
    public Map<LocalDateTime,LocalDateTime> getFlyingTimeByFlightNoList(@PathVariable List<String> flightIdList) {

        log.info("查询航班出发到达时间，参数：{}", flightIdList);
        Map<LocalDateTime, LocalDateTime> map = flightService.list(new QueryWrapper<Flight>()
                        .select("departure_time", "arrival_time")
                        .in("flight_id", flightIdList))
                .stream()
                .collect(Collectors.toMap(Flight::getDepartureTime, Flight::getArrivalTime));

        return map;

    }

}