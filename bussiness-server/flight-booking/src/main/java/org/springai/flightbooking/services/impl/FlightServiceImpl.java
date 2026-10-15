package org.springai.flightbooking.services.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.power.exception.handler.BusinessException;
import org.jetbrains.annotations.NotNull;
import org.springai.flightbooking.mapper.FlightMapper;
import org.springai.flightbooking.pojo.Flight;
import org.springai.flightbooking.pojo.Plane;
import org.springai.flightbooking.services.FlightService;
import org.springai.flightbooking.services.PlaneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class FlightServiceImpl extends ServiceImpl<FlightMapper, Flight> implements FlightService {

    @Autowired
    private FlightMapper flightMapper;

    @Autowired
    private PlaneService planeService;


    @Override
    public List<Flight> selectFlightByCondition(String flightId, String planeType, LocalDateTime departureTime, String departureCity, String arrivalCity) {
        return List.of();
    }

    @Override
    public void updateFlight(Flight flight) {

        Long planeId = flight.getPlaneId();
        List<Plane> planes = planeService.selectPlaneByCondition(planeId, null);

        if (planes!=null && !CollectionUtils.isEmpty(planes))
        {
            Plane plane = planes.get(0);
            Integer totalSeats = plane.getTotalSeats();
            if (totalSeats < flight.getRealSeats())
                throw new BusinessException("客机座位数不足");
            flight.setPlaneType(plane.getPlaneType());
            flight.setTotalSeats(totalSeats);
            int updated = flightMapper.updateById(flight);
            if (updated == 0)
                throw new BusinessException("修改失败，请联系管理员");
        }
    }

    @Override
    public void deleteFlightById(Long id) {
        int deleted = flightMapper.deleteById(id);
        if (deleted == 0) {
            throw new BusinessException("删除失败，请联系管理员");
        }

    }

    @Override
    public void addFlight(Long planeId, LocalDateTime departureTime, LocalDateTime arrivalTime, String departureCity, String arrivalCity) {
        StringBuilder sb = getFlightIdStrings();
        String flightId = sb.toString();

        List<Plane> planes = planeService.selectPlaneByCondition(planeId, null);
        if (planes!=null && !CollectionUtils.isEmpty(planes))
        {
            Plane plane = planes.get(0);
            Flight flight = new Flight(null, flightId, planeId, plane.getPlaneType(), departureTime, arrivalTime, departureCity, arrivalCity, plane.getTotalSeats(),0, 1,0);
            int inserted = flightMapper.insert(flight);
            if (inserted == 0)
                throw  new BusinessException("添加失败,请联系管理员！");
        }else
            throw  new BusinessException("不存在该客机，请重新添加！");
    }



    @NotNull // 返回值不能为null
    protected static StringBuilder getFlightIdStrings() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(7);
        for (int i = 0; i < 7; i++) {
            sb.append(chars.charAt(ThreadLocalRandom.current().nextInt(chars.length())));
        }
        return sb;
    }
}