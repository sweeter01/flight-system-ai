package com.power.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.power.pojo.CrewGroup;
import com.power.vo.CrewGroupVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface CrewGroupMapper extends BaseMapper<CrewGroup> {

    IPage<CrewGroupVo> selectCrewGroupPage(Page<CrewGroupVo> page,
                                           @Param("groupName") String groupName,
                                           @Param("groupCode") String groupCode,
                                           @Param("status") Integer status,
                                           @Param("leaderId") Long leaderId,
                                           @Param("baseCityId") Long baseCityId);


    @Select("select group_name from tb_crew_group where id = #{groupId} and is_deleted = 0")
    String getGroupNameByGroupId(Long groupId);
}