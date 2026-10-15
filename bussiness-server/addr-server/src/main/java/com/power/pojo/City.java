package com.power.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@TableName("tb_city")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class City {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String cityCode;

    private String cityName;

    private String airportName;

    private Integer status;

    private LocalDateTime createTime;

    @TableLogic
    private Integer isDeleted;
}