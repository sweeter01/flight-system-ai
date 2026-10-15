package org.springai.flightbooking.services.impl;

import org.springai.flightbooking.services.FlightPriceService;

import org.springai.flightbooking.pojo.FlightPrice;
import org.springai.flightbooking.mapper.FlightPriceMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class FlightPriceServiceImpl extends ServiceImpl<FlightPriceMapper, FlightPrice> implements FlightPriceService {
}
