package com.power.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.power.mapper.AddrMapper;
import com.power.pojo.City;
import com.power.service.AddrService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddrServiceImpl extends ServiceImpl<AddrMapper, City> implements AddrService {

    @Override
    public List<City> selectCityList(Long id, String cityCode) {
        return List.of();
    }

    @Override
    public void addCity(City city) {

    }

    @Override
    public void updateCityById(City city) {

    }

    @Override
    public void deleteCityById(Long id) {

    }
}
