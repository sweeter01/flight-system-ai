package com.power.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.power.pojo.Passenger;

import java.util.List;

public interface PassengerMapper extends BaseMapper<Passenger> {

    List<Passenger> getPassengerInfoPages(
                               String name, Integer ageMin, Integer ageMax, String sex, String certificate, String username);
}
