package com.power.controller;

import cn.hutool.crypto.digest.MD5;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.power.model.Result;
import com.power.pojo.Passenger;
import com.power.service.PassengerService;
import com.power.vo.OrderVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Api(tags = "乘客基本信息管理接口")
@RestController
@RequestMapping("/passenger")
@Slf4j
public class PassengerController {

    @Autowired
    private PassengerService passengerService;

    @GetMapping("/list")
    @ApiOperation(value = "查询所有乘客")
    @PreAuthorize("hasAuthority('passenger:list')")
    public Result<Page<Passenger>> getPassengerInfoPages(
            String name,
            Integer ageMin,
            Integer ageMax,
            String sex,
            String certificate,
            String username,
            @RequestParam(name = "current", defaultValue = "1")
            Integer current,
            @RequestParam(name = "size", defaultValue = "10")
            Integer size

    ) {
        log.info("查询所有乘客信息，查询条件：姓名={},年龄下限={},年龄上限={},性别={},身份证号={},用户名={},当前页={},每页数量={}",
                name, ageMin, ageMax, sex, certificate, username, current, size);

        return  Result.success(
                passengerService.getPassengerInfoPages(name, ageMin, ageMax, sex, certificate, username, current, size));

    }

    @GetMapping("/orderVoList")
    @ApiOperation(value = "根据乘客ID查询乘客的所有订单详情")
    public Result<List<OrderVo>> getPassengerOrderVo(
             Long passengerId
    ) {
        log.info("查询乘客的所有订单信息，乘客ID={}", passengerId);
        return Result.success(passengerService.getOrderVoByPassengerId(passengerId));
    }

    @PutMapping("/update")
    @ApiOperation(value = "更新乘客信息")
    @PreAuthorize("hasAuthority('passenger:update')")
    public Result updatePassenger(@RequestBody Passenger passenger) {
        log.info("更新乘客基本信息 {}", passenger);
        passenger.setUpdateTime(LocalDateTime.now());
        passengerService.updateById(passenger);
        return Result.success();
    }

    @PostMapping("/add")
    @ApiOperation(value = "添加乘客信息")
    @PreAuthorize("hasAuthority('passenger:add')")
    public Result addPassenger(@RequestBody Passenger passenger) {
        log.info("添加乘客基本信息 {}", passenger);
        passenger.setPassword(MD5.create().digestHex(passenger.getPassword()));
        passenger.setCreateTime(LocalDateTime.now());
        passenger.setUpdateTime(LocalDateTime.now());
        passengerService.save(passenger);
        return Result.success();
    }
    @DeleteMapping("/delete")
    @ApiOperation(value = "删除乘客信息")
    @PreAuthorize("hasAuthority('passenger:delete')")
    public Result deletePassenger(@RequestParam Long passengerId) {
        log.info("删除乘客基本信息 {}", passengerId);
        passengerService.removeById(passengerId);
        return Result.success();
    }


}