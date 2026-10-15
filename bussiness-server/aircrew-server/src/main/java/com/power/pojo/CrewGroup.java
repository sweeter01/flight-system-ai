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
@TableName("tb_crew_group")
public class CrewGroup {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("group_code")
    private String groupCode;

    @TableField("group_name")
    private String groupName;

    @TableField("leader_id")
    private Long leaderId;

    @TableField("base_city_id")
    private Long baseCityId;

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