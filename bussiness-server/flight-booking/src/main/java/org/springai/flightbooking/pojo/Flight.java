package org.springai.flightbooking.pojo;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("tb_flight")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Flight
{
    @TableId(type = IdType.INPUT)
    public  Long id;

    public String flightId;

    public Long planeId;

    public String planeType;

    public LocalDateTime departureTime;

    public LocalDateTime arrivalTime;

    public String departureCity;

    public String arrivalCity;

    public Integer totalSeats;

    public Integer realSeats;

    public Integer status;

    @TableLogic
    public Integer isDeleted;
}