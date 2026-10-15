package com.power.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("tb_crew_schedule")
public class CrewSchedule {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("pilot_id")
    private Long pilotId;

    @TableField("assistant_id")
    private Long assistantId;

    @TableField("flight_id")
    private String flightId;

    @TableField("duty_type")
    private Integer dutyType;

    @TableField("status")
    private Integer status;

    @TableField("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @TableField("update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @TableField("is_deleted")
    @TableLogic
    private Integer isDeleted;
}