package com.power.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.power.pojo.City;
import com.power.pojo.Crew;
import com.power.vo.CrewBaseVo;
import com.power.vo.CrewVo;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public interface CrewService  extends IService<Crew> {


    void assignCrewToFlight(Crew crew);

    Page<CrewVo> listCrewByPage(String name, Integer gender, String crewNo, String positionType,
                                Long groupId, Long baseCityId, Long current, Long size);

    void updateCrew(Crew crew);

    void deleteCrew(List<Long> ids);

    List<City> getCitiesByIds(List<Long> cityIds);

    Map<Long,String> getCityIdAndName();

    LinkedHashMap<String,List<CrewBaseVo>> getCrewScheduleCrewDetail(int[] ids);

}