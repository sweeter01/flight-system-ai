package com.power.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.power.pojo.Crew;
import com.power.pojo.CrewGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrewGroupVo {

    private Long id;

    private String groupCode;

    private String groupName;

    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    private String baseCityName;
    private String leaderName;

}