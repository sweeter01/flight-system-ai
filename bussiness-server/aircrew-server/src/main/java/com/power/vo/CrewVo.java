package com.power.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.power.pojo.City;
import com.power.pojo.CrewGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.pl.NIP;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrewVo {

    private Long id;

    private String crewNo;

    private String name;

    private Integer gender;

    private String phone;

    private String positionName; // 职务名称

    private Long groupId;  // 所属航班分组ID，必须，这是建立关联关系的字段（外键）
    private  Long baseCityId;  // 基础城市ID，必须，这是建立关联关系的字段（外键）

    private CrewGroup crewGroup;  // 所属航班分组实体类
    private City city;  // 基地城市实体类


    private Integer status;

    private String licenseNo;

    private Integer flightHours;
    private LocalDate hireDate;

    private  Integer flightCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
