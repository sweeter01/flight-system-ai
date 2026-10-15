package org.springai.flightbooking.client;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.power.model.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springai.flightbooking.pojo.FlightPrice;
import org.springai.flightbooking.services.FlightPriceService;
import org.springai.flightbooking.services.impl.FlightPriceServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Api(tags = "航班价格管理")
@RestController
@Slf4j
@RequestMapping("/flightPrice")
public class FlightPriceController {


    @Autowired
    private FlightPriceService flightPriceService;
    @Autowired
    private FlightPriceServiceImpl flightPriceServiceImpl;

    @GetMapping("/list")
    @ApiOperation(value = "查询所有航班价格")
    @PreAuthorize("hasAuthority('flightPrice:list')")
    public Result<Page> getFlightPricePages(
            Long flightId,
            String seatType,
            Double lowPrice,
            Double highPrice,
            Integer status,
            @RequestParam(name = "current",defaultValue = "1") Long current,
            @RequestParam(name = "size",defaultValue = "10") Long size

    ) {
        log.info("查询所有价格参数:{}",flightId,seatType,lowPrice,highPrice,status,current,size);
        Page<FlightPrice> page = new Page<>(current, size);

        page= flightPriceService.page(page,new LambdaQueryWrapper<FlightPrice>()
                .eq(flightId!=null,FlightPrice::getFlightId,flightId)
                .eq(seatType!=null,FlightPrice::getSeatType,seatType)
                .eq(status!=null,FlightPrice::getStatus,status)
                .between(lowPrice!=null && highPrice!=null,
                        FlightPrice::getPrice,lowPrice,highPrice)
        );
        return  Result.success(page);

    }
    @PostMapping("/add")
    @ApiOperation(value = "添加航班价格")
    @PreAuthorize("hasAuthority('flightPrice:add')")
    public  Result addFlightPrice(@RequestBody FlightPrice flightPrice){
        log.info("添加航班价格参数:{}",flightPrice);
        flightPriceService.save(flightPrice);
        return Result.success("添加航班价格成功");
    }

    @PutMapping("/update")
    @ApiOperation(value = "更新航班价格")
    @PreAuthorize("hasAuthority('flightPrice:update')")
    public  Result updateFlightPrice(@RequestBody FlightPrice flightPrice){
        log.info("更新航班价格参数:{}",flightPrice);
        flightPriceService.updateById(flightPrice);
        return Result.success("更新航班价格成功");
    }

    @DeleteMapping("/delete")
    @ApiOperation(value = "删除航班价格")
    @PreAuthorize("hasAuthority('flightPrice:delete')")
    public  Result deleteFlightPrice(@RequestParam("id") Long id){
        log.info("删除航班价格参数:{}",id);
        flightPriceService.removeById(id);
        return Result.success("删除航班价格成功");
    }

    /// /////////  feign 调用 ///////////////

    @GetMapping("/orderPrice")
    @ApiOperation(value = "查询订单航班价格")
    public  List<Double>  getOrderPrices(@RequestParam("flightIds") List<String> flightIds, @RequestParam("seatTypes") List<String> seatTypes){

        log.info("查询订单航班价格参数:{}",flightIds,seatTypes);
        List<FlightPrice> flightPrices = flightPriceService.list(new LambdaQueryWrapper<FlightPrice>()
                .in(FlightPrice::getFlightId, flightIds)
                .in(FlightPrice::getSeatType, seatTypes)
                .eq(FlightPrice::getStatus, 1));
        List<Double> prices = flightPrices.stream().map(FlightPrice::getPrice).toList();
        return prices;

    }
}