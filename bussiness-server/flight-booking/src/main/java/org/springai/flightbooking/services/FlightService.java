package org.springai.flightbooking.services;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springai.flightbooking.pojo.Flight;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;


public interface FlightService extends IService<Flight> {

    List<Flight> selectFlightByCondition(String flightId,
                                         String planeType,
                                         LocalDateTime departureTime,
                                         String departureCity,String arrivalCity);

    void updateFlight(Flight flight);


    void  deleteFlightById(Long id);


    void addFlight(Long planeId,LocalDateTime departureTime,LocalDateTime arrivalTime, String departureCity,String arrivalCity);

}
