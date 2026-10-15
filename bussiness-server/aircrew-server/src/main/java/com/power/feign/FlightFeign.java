package com.power.feign;

import com.power.feign.impl.FlightFeignImpl;
import com.power.model.Result;
import org.hibernate.validator.internal.util.privilegedactions.LoadClass;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@FeignClient(name = "flight-booking",fallback = FlightFeignImpl.class)
public interface FlightFeign {

    @GetMapping("/flight/getFlyingTime/{flightIdList}")
    public Map<LocalDateTime,LocalDateTime> getFlyingTimeByFlightNoList(@PathVariable List<String> flightIdList);
}