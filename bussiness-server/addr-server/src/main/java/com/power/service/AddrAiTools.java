package com.power.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.power.pojo.City;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class AddrAiTools {

    @Autowired
    private AddrService addrService;

    @Tool(description = "查询城市列表，根据城市编码或城市名称查询，最多返回100条记录")
    public List<City> queryCityList(
            @ToolParam(description = "城市编码,可为空", required = false) String cityCode,
            @ToolParam(description = "城市名称,可为空", required = false) String cityName) {
        log.info("AI工具-查询城市列表: cityCode={}, cityName={}", cityCode, cityName);
        return addrService.list(new LambdaQueryWrapper<City>()
                .eq(cityCode != null && !cityCode.isEmpty(), City::getCityCode, cityCode)
                .like(cityName != null && !cityName.isEmpty(), City::getCityName, cityName)
                .last("limit 100"));
    }

    @Tool(description = "新增运营城市，当用户要求添加城市、新增运营城市时使用此工具")
    public String addCity(
            @ToolParam(description = "城市编码，如PEK、SHA等") String cityCode,
            @ToolParam(description = "城市名称，如北京、上海等") String cityName,
            @ToolParam(description = "机场名称") String airportName) {
        City city = new City();
        city.setCityCode(cityCode);
        city.setCityName(cityName);
        city.setAirportName(airportName);
        city.setStatus(1);
        log.info("AI工具-新增城市: {}", city);
        addrService.save(city);
        return "城市新增成功";
    }

    @Tool(description = "更新运营城市信息，当用户要求修改城市信息时使用此工具")
    public String updateCity(
            @ToolParam(description = "城市ID，不为空") Long id,
            @ToolParam(description = "城市编码，可为空") String cityCode,
            @ToolParam(description = "城市名称，可为空") String cityName,
            @ToolParam(description = "机场名称，可为空") String airportName,
            @ToolParam(description = "运营状态，1运营中/0暂停运营，可为空") Integer status) {
        City city = new City();
        city.setId(id);
        city.setCityCode(cityCode);
        city.setCityName(cityName);
        city.setAirportName(airportName);
        city.setStatus(status);
        log.info("AI工具-更新城市: {}", city);
        addrService.updateById(city);
        return "城市更新成功";
    }

    @Tool(description = "删除运营城市，当用户要求删除、移除城市时使用此工具")
    public String deleteCity(
            @ToolParam(description = "城市ID，不为空") Long id) {
        log.info("AI工具-删除城市: {}", id);
        addrService.removeById(id);
        return "城市删除成功";
    }
}