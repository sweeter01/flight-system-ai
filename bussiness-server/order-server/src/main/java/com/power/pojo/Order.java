package com.power.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@TableName("tb_order")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderCode;

    private Long bookingNumber;

    private Long passengerId;

    private String passengerName;

    private Double discount;

    private String couponCode;

    private String paymentMethod;

    private String comment;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    private Integer status;

    @TableLogic
    private Integer isDeleted;
}