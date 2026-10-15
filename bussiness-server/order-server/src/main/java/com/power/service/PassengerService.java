package com.power.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.power.model.Result;
import com.power.pojo.Passenger;
import com.power.vo.OrderVo;
import com.power.vo.PassengerVo;

import java.util.List;

public interface PassengerService extends IService<Passenger> {


    Page<Passenger> getPassengerInfoPages(
                                            String name, Integer ageMin, Integer ageMax,
                                            String sex,
                                            String certificate,
                                            String username,
                                            Integer current, Integer size);

    List<OrderVo> getOrderVoByPassengerId(Long passengerId);
}
