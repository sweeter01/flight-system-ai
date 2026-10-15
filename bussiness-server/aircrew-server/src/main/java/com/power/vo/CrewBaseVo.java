package com.power.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CrewBaseVo {

    private Long id;

    private String crewNo;

    private String name;

    private Integer gender;

    private String phone;

    private String positionName; // 职务名称

}
