package com.power.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.power.pojo.CrewSchedule;
import com.power.vo.CrewScheduleVo;

import java.util.List;

public interface CrewScheduleMapper extends BaseMapper<CrewSchedule> {
    IPage<CrewScheduleVo> getInfoByPageMapper(Page<CrewScheduleVo> page,
                                              String flightId,
                                              Long pilotId,
                                              Long assistantId,
                                              Integer status,
                                              Integer dutyType,
                                              Long current,
                                              Long size);
}
