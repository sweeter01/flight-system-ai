package com.power.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.power.enums.CrewPosition;
import com.power.feign.AddrFeign;
import com.power.mapper.CrewGroupMapper;
import com.power.mapper.CrewMapper;
import com.power.pojo.City;
import com.power.pojo.Crew;
import com.power.pojo.CrewGroup;
import com.power.service.CrewGroupService;
import com.power.service.CrewScheduleService;
import com.power.service.CrewService;
import com.power.vo.CrewBaseVo;
import com.power.vo.CrewVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CrewServiceImpl extends ServiceImpl<CrewMapper, Crew> implements CrewService {

     private   final    LinkedHashMap<String,List<CrewBaseVo>> crewMap = new LinkedHashMap<>();


    @Resource
    private CrewGroupService crewGroupService;


    @Autowired
    private AddrFeign addrFeign;
    @Autowired
    private CrewMapper crewMapper;


    @Autowired
    private CrewGroupMapper crewGroupMapper;



    @Override
    public void updateCrew(Crew crew) {
        fillBaseCityId(crew);
        updateGroupLeaderIfNeeded(crew);
        crewMapper.updateById(crew);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCrew(List<Long> ids) {
        List<Crew> crews = crewMapper.selectList(new LambdaQueryWrapper<Crew>()
                .in(Crew::getId, ids));

        List<Long> groupIdList = crews.stream()
                .filter(each ->
                        each.getPositionType().equals(CrewPosition.PILOT.name())
                                || each.getPositionType().equals(CrewPosition.FLIGHT_ATTENDANT_LEADER.name())
                ).map(Crew::getGroupId).toList();
        if (!groupIdList.isEmpty()) {
             crewGroupMapper.update(new LambdaUpdateWrapper<CrewGroup>()
                    .set(CrewGroup::getLeaderId, null)
                    .in(CrewGroup::getId, groupIdList));
            log.info("删除分组[{}] 的 leader", groupIdList);
        }

        log.info("删除 crew[{}]", ids);
        crewMapper.update(new LambdaUpdateWrapper<Crew>()
                .set(Crew::getIsDeleted, 1)
                .in(Crew::getId, ids));

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignCrewToFlight(Crew crew) {
        log.info("assignCrewToFlight: {}", crew);
        fillBaseCityId(crew);
        crew.setCreateTime(LocalDateTime.now());
        super.save(crew);
        updateGroupLeaderIfNeeded(crew);
    }

    private void fillBaseCityId(Crew crew) {
        Long baseCityId = crewGroupService.getObj(new LambdaQueryWrapper<CrewGroup>()
                .select(CrewGroup::getBaseCityId)
                .eq(CrewGroup::getId, crew.getGroupId()), o -> (Long) o);
        crew.setBaseCityId(baseCityId);
    }

    private void updateGroupLeaderIfNeeded(Crew crew) {
        String positionType = crew.getPositionType();
        boolean isLeaderPosition = CrewPosition.PILOT.name().equals(positionType)
                || CrewPosition.FLIGHT_ATTENDANT_LEADER.name().equals(positionType);
        if (isLeaderPosition && crew.getGroupId() != null) {
            CrewGroup group = new CrewGroup();
            group.setId(crew.getGroupId());
            group.setLeaderId(crew.getId());
            crewGroupService.updateById(group);
            log.info("crew[{}] 担任分组[{}] 的 leader", crew.getId(), crew.getGroupId());
        }
    }

    @Override
    public Page<CrewVo> listCrewByPage(String name, Integer gender, String crewNo, String positionType,
                                      Long groupId, Long baseCityId, Long current, Long size) {
        Page<Crew> crewPage = new Page<>(current, size);
        LambdaQueryWrapper<Crew> queryWrapper = new LambdaQueryWrapper<Crew>()
                .like(StringUtils.hasText(name), Crew::getName, name)
                .like(StringUtils.hasText(crewNo), Crew::getCrewNo, crewNo)
                .eq(gender != null, Crew::getGender, gender)
                .eq(StringUtils.hasText(positionType), Crew::getPositionType, positionType)
                .eq(groupId != null, Crew::getGroupId, groupId)
                .eq(baseCityId != null, Crew::getBaseCityId, baseCityId);

        crewPage = page(crewPage, queryWrapper);

        log.info("查询到的机组人员信息: {}", crewPage.getRecords());

        ArrayList<CrewVo> crewVos = new ArrayList<>();
        crewPage.getRecords().forEach(each -> {
            CrewVo crewVo = new CrewVo();
            BeanUtils.copyProperties(each, crewVo);
            crewVo.setPositionName(CrewPosition.valueOf(each.getPositionType()).getDescription());
            crewVos.add(crewVo);
        });

        List<Long> groupIds = crewPage.getRecords().stream().map(Crew::getGroupId).collect(Collectors.toList());
        if (!groupIds.isEmpty()) {
            List<CrewGroup> crewGroups = crewGroupService.listByIds(groupIds);
            Map<Long, CrewGroup> groupMaps = crewGroups.stream().collect(Collectors.toMap(CrewGroup::getId, group -> group));
            crewVos.forEach(each -> {
                CrewGroup crewGroup = groupMaps.get(each.getGroupId());
                if (!ObjectUtils.isEmpty(crewGroup)) {
                    each.setCrewGroup(crewGroup);
                }
            });
        }

        List<Long> cityIds = crewVos.stream()
                .map(CrewVo::getBaseCityId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        if (!cityIds.isEmpty()) {
            List<City> cities = getCitiesByIds(cityIds);
            Map<Long, City> cityMap = cities.stream().collect(Collectors.toMap(City::getId, c -> c));
            crewVos.forEach(vo -> {
                City city = cityMap.get(vo.getBaseCityId());
                if (city != null) {
                    vo.setCity(city);
                }
            });
        }

        Page<CrewVo> voPage = new Page<>(crewPage.getCurrent()
                , crewPage.getSize(), crewPage.getTotal());
        return voPage.setRecords(crewVos);
    }


    //调用feign接口查询所有cityId对应的城市信息
    @Override
    public List<City> getCitiesByIds(List<Long> cityIds) {
        if (cityIds == null || cityIds.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return addrFeign.getCityListByIds(cityIds);
        } catch (Exception e) {
            log.warn("Feign调用查询城市信息失败: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public Map<Long, String> getCityIdAndName() {
        try {
            return addrFeign.getCityIdAndName();
        } catch (Exception e) {
            log.warn("Feign调用查询城市列表失败: {}", e.getMessage());
            return Collections.emptyMap();
        }

    }


    /**
     * 查询航班日程安排的机组人员详情信息
     * 包括飞行队和客舱机组人员
     * @param ids 机组人员id数组
     * @return 机组人员详情信息列表
     */

    @Override
    public LinkedHashMap<String,List<CrewBaseVo>> getCrewScheduleCrewDetail(int[] ids) {

        System.out.println(ids.length);
        if (ids == null || ids.length == 0) {
            return new LinkedHashMap<>();
        }

        /**
         * 查询飞行队详情信息
         */
        int pilotId = ids[0];
        Long pilotGroupId = crewMapper.selectOne(new LambdaQueryWrapper<Crew>()
                .eq(Crew::getId, pilotId)).getGroupId();
        String pilotGroupName = crewGroupMapper.getGroupNameByGroupId(pilotGroupId);
        List<Crew> pilotCrews = crewMapper.selectList(new LambdaQueryWrapper<Crew>()
                .eq(Crew::getGroupId, pilotGroupId)
                .in(Crew::getPositionType, Arrays.asList(CrewPosition.PILOT.name(), CrewPosition.COPILOT.name()))

        );
        //查询飞行队详情信息Map-> 队名：机组人员列表
        catchGroupMemberList(pilotGroupName,pilotCrews);

        /**
         * 查询乘务大队详情信息
         */
        int assistantId = ids[1];

        Long assistantGroupId = crewMapper.selectOne(new LambdaQueryWrapper<Crew>()
                .eq(Crew::getId, assistantId)).getGroupId();
        String assistantGroupName = crewGroupMapper.getGroupNameByGroupId(assistantGroupId);
        List<Crew> assistantCrews = crewMapper.selectList(new LambdaQueryWrapper<Crew>()
                .eq(Crew::getGroupId, assistantGroupId)
                .in(Crew::getPositionType, Arrays.asList(CrewPosition.FLIGHT_ATTENDANT.name(), CrewPosition.FLIGHT_ATTENDANT_LEADER.name())));
        catchGroupMemberList(assistantGroupName,assistantCrews); //
        return crewMap;

    }

    /**
     * 函数描述：查询机组人员详情信息到具体的分组
     * @param assistantCrews 机组人员列表
     */
    private void catchGroupMemberList(String groupName, List<Crew> assistantCrews) {
        ArrayList<CrewBaseVo> assistantCrewBaseVos = new ArrayList<>();
        assistantCrews.forEach(each -> {
            CrewBaseVo crewBaseVo = new CrewBaseVo();
            BeanUtils.copyProperties(each, crewBaseVo);
            crewBaseVo.setPositionName(CrewPosition.valueOf(each.getPositionType()).getDescription());
            assistantCrewBaseVos.add(crewBaseVo);
        });
        crewMap.put(groupName, assistantCrewBaseVos);
    }


}