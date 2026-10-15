package com.power.feign;

import com.power.feign.impl.AddrFeignImpl;
import com.power.pojo.City;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@FeignClient(name = "addr-server", path = "/addr", fallback = AddrFeignImpl.class)
public interface AddrFeign {

    @GetMapping("/listCityFeign/{ids}")
    public List<City> getCityListByIds(@PathVariable("ids") List<Long> ids);

    @GetMapping("/listUsefulCity")
    public Map<Long,String> getCityIdAndName();
}