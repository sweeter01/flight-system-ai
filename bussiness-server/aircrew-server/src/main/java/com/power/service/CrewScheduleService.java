package com.power.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.power.pojo.CrewSchedule;
import com.power.vo.CrewScheduleVo;

import java.util.List;

public interface CrewScheduleService extends IService<CrewSchedule> {

     Page<CrewScheduleVo> getInfoByPage(
            String flightId,
            Long pilotId,
            Long assistantId,
            Integer status,
            Integer dutyType,
            Long current,
            Long size
    );

     void updateCrewSchedule(CrewSchedule crewSchedule);

     void deleteCrewSchedule(List<Long> ids);
}
