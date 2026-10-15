package com.power.feign.impl;

import com.power.feign.FlightFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@Component
@Slf4j
public class FlightFeignImpl implements FlightFeign {
    @Override
    public Map<LocalDateTime, LocalDateTime> getFlyingTimeByFlightNoList(@PathVariable List<String> flightNoList) {
        log.warn("远程调用查询航班飞行时间区间接口失败: {}", flightNoList);
        return Map.of();
    }
}