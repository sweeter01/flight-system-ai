package com.power.feign.impl;

import com.power.feign.AddrFeign;
import com.power.pojo.City;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class AddrFeignImpl implements AddrFeign {
    @Override
    public List<City> getCityListByIds(List<Long> ids) {

        log.info("根据城市ID列表:{}查询城市列表",ids);

        return  Collections.emptyList();
    }

    @Override
    public Map<Long, String> getCityIdAndName() {
        log.info("查询所有在运营中的城市列表");
        return Collections.emptyMap();
    }
}