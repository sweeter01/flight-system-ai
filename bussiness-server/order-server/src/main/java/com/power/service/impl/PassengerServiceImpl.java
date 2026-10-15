package com.power.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.power.mapper.PassengerMapper;
import com.power.pojo.Passenger;
import com.power.service.PassengerService;
import com.power.vo.OrderVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class PassengerServiceImpl extends ServiceImpl<PassengerMapper, Passenger> implements PassengerService {

    @Autowired
    private PassengerMapper passengerMapper;

    @Autowired
    private OrderServiceImpl orderServiceImpl;

    @Override
    public Page<Passenger> getPassengerInfoPages(String name, Integer ageMin, Integer ageMax, String sex, String certificate, String username, Integer current, Integer size) {

        log.info("根据乘客信息查询乘客列表，姓名={},年龄范围={},性别={},证件号={},用户名={}",
                name, ageMin, ageMax, sex, certificate, username);
        Page<Passenger> page = new Page<>(current, size);
        page=passengerMapper.selectPage(page, new LambdaQueryWrapper<Passenger>()
                .like(name!=null,Passenger::getName, name)
                .between(ageMin!=null&&ageMax!=null,Passenger::getAge, ageMin, ageMax)
                .eq(sex!=null,Passenger::getSex, sex)
                .like(certificate!=null,Passenger::getCertificate, certificate)
                .like(username!=null,Passenger::getUsername, username)
        );
        return page;
    }

    @Override
    public List<OrderVo> getOrderVoByPassengerId(Long passengerId) {
        log.info("根據乘客ID查询乘客的所有订单詳情，乘客ID={}", passengerId);
        return orderServiceImpl.getPassengerOrderVo(List.of(passengerId));
    }


}