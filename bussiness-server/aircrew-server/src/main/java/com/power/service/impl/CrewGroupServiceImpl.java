package com.power.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.power.exception.handler.BusinessException;
import com.power.mapper.CrewGroupMapper;
import com.power.mapper.CrewMapper;
import com.power.pojo.Crew;
import com.power.pojo.CrewGroup;
import com.power.service.CrewGroupService;
import com.power.vo.CrewGroupVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CrewGroupServiceImpl extends ServiceImpl<CrewGroupMapper, CrewGroup> implements CrewGroupService {



    @Autowired
    private CrewMapper crewMapper;
    @Override
    public Page<CrewGroupVo> getCrewGroupPage(String groupName, String groupCode, Integer status,
                                             Long leaderId, Long baseCityId, Long current, Long size) {
        Page<CrewGroupVo> page = new Page<>(current, size);
        return (Page<CrewGroupVo>) baseMapper.selectCrewGroupPage(page, groupName, groupCode, status, leaderId, baseCityId);
    }

    @Override
    public void deleteCrewGroup(Long id) {
        Long count = crewMapper.selectCount(new LambdaQueryWrapper<Crew>().eq(Crew::getGroupId, id));
        if (count > 0) {
            throw new BusinessException("该分组下有成员，不能删除");
        }else baseMapper.deleteById(id);
    }
}