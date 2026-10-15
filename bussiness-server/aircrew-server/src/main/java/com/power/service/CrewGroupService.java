package com.power.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.power.pojo.CrewGroup;
import com.power.vo.CrewGroupVo;

public interface CrewGroupService extends IService<CrewGroup> {

    Page<CrewGroupVo> getCrewGroupPage(String groupName, String groupCode, Integer status,
                                      Long leaderId, Long baseCityId, Long current, Long size);

    void deleteCrewGroup(Long id);
}