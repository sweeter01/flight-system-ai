package com.power.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("tb_crew")
public class Crew {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("crew_no")
    private String crewNo;

    @TableField("name")
    private String name;

    @TableField("gender")
    private Integer gender;

    @TableField("phone")
    private String phone;

    @TableField("position_type")
    private String positionType;

    @TableField("group_id")
    private Long groupId;

    @TableField("base_city_id")
    private Long baseCityId;

    @TableField("status")
    private Integer status;

    @TableField("license_no")
    private String licenseNo;

    @TableField("flight_hours")
    private Integer flightHours;

    @TableField("hire_date")
    private LocalDate hireDate;

    @TableField("is_deleted")
    @TableLogic
    private Integer isDeleted;

    @TableField("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @TableField("update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}