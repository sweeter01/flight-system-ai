package com.power.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.power.pojo.Order;
import com.power.pojo.Passenger;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PassengerVo extends Passenger {

        @JsonIgnore
        private String password;
        @JsonIgnore
        private Long bookingNumber;
        @JsonIgnore
        private  Long passengerId;

}