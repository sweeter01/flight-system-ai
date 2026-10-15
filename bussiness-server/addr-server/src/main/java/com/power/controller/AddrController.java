package com.power.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.power.model.Result;
import com.power.pojo.City;
import com.power.service.AddrService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@Slf4j
@Api("开通城市信息")
@RequestMapping("/addr")
public class AddrController {
    @Autowired
    private AddrService addrService;
    @ApiOperation("查询城市列表")
    @GetMapping("/listCity")
    @PreAuthorize("hasAuthority('addr:listCity')")
    public Result<Page<City>> listCity(
            String cityCode,
            String cityName,
            Long current, Long size){

        log.info("查询城市列表,根据编码:{}、名称:{}查询城市列表",cityCode,cityName);
        Page<City> cityPage = new Page<>(current == null ? 1L : current, size == null ? 10L : size);
        cityPage = addrService.page(cityPage, new LambdaQueryWrapper<City>()
                .eq(cityCode != null && !cityCode.isEmpty(), City::getCityCode, cityCode)
                .like(cityName != null && !cityName.isEmpty(), City::getCityName, cityName));
        return Result.success(cityPage);
    }

    @ApiOperation("新增运营城市")
    @PostMapping("/addCity")
    @PreAuthorize("hasAuthority('addr:addCity')")
    public Result addCity(String cityCode, String cityName, String airportName){

        City city = new City();
        city.setCityCode(cityCode);
        city.setCityName(cityName);
        city.setAirportName(airportName);
        city.setStatus(1);
        log.info("新增城市: {}", city);
        addrService.save(city);
        return Result.success();
    }

    @ApiOperation("更新城市")
    @PutMapping("/updateCity")
    @PreAuthorize("hasAuthority('addr:updateCity')")
    public Result updateCity(Long id, String cityCode, String cityName, String airportName, Integer status){
        City city = new City();
        city.setId(id);
        city.setCityCode(cityCode);
        city.setCityName(cityName);
        city.setAirportName(airportName);
        city.setStatus(status);
        log.info("更新城市: {}", city);
        addrService.updateById(city);
        return Result.success();
    }

    @ApiOperation("删除城市")
    @DeleteMapping("/deleteCity")
    @PreAuthorize("hasAuthority('addr:deleteCity')")
    public Result deleteCity(@RequestParam("id") Long id){
        log.info("删除城市: {}", id);
        addrService.removeById(id);
        return Result.success();
    }

    /// ///////// feign调用 查询城市列表接口 /////////

    @GetMapping("/listCityFeign/{ids}")
    @ApiOperation("根据城市ID列表查询城市列表")
    public List<City> getCityListByIds(@PathVariable("ids") List<Long> ids){

        log.info("根据城市ID列表:{}查询城市列表",ids);
        List<City> cities = addrService.listByIds(ids);
        cities.forEach(city -> System.out.println(city.getCityName()));
        return  cities;
    }

    @GetMapping("/listUsefulCity")
    @ApiOperation("查询所有在运营中的基地城市信息")
    public Map<Long,String> getCityIdAndName() {
        log.info("查询所有在运营中的基地城市信息");
        Map<Long, String> map = addrService.list(new QueryWrapper<City>()
                        .select("id", "city_name")
                        .eq("status", 1))
                .stream()
                .collect(Collectors.toMap(city -> city.getId(), City::getCityName));
        return map;

    }
}