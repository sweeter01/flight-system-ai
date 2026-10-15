package com.power.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.power.pojo.City;

import java.util.List;

public interface AddrService extends IService<City> {

    List<City> selectCityList(Long id,String cityCode);

    void addCity(City city);

    void updateCityById(City city);

    void deleteCityById(Long id);
}
