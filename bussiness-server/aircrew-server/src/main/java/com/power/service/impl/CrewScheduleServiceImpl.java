package com.power.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.power.constant.BusinessEnum;
import com.power.exception.handler.BusinessException;
import com.power.feign.FlightFeign;
import com.power.feign.impl.FlightFeignImpl;
import com.power.mapper.CrewScheduleMapper;
import com.power.model.Result;
import com.power.pojo.CrewSchedule;
import com.power.service.CrewScheduleService;
import com.power.vo.CrewScheduleVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class CrewScheduleServiceImpl extends ServiceImpl<CrewScheduleMapper, CrewSchedule> implements CrewScheduleService {

    @Autowired
    private  CrewScheduleMapper crewScheduleMapper;

    @Autowired
    private FlightFeign flightFeign;

    @Override
    public Page<CrewScheduleVo> getInfoByPage(String flightId, Long pilotId, Long assistantId, Integer status, Integer dutyType, Long current, Long size) {
        log.info("查询日程安排接口service启动了");
        Page<CrewScheduleVo> page = new Page<>(current, size);
        return (Page<CrewScheduleVo>) crewScheduleMapper.getInfoByPageMapper(page,flightId, pilotId, assistantId, status, dutyType,current,size);

    }

    @Override
    public void updateCrewSchedule(CrewSchedule crewSchedule) {
        log.info("更新日程安排接口service启动了");
        crewSchedule.setUpdateTime(LocalDateTime.now());

//        /** feign调用航班服务获取飞行时间 */
        Boolean b = compareFlightTime(List.of(crewSchedule.getFlightId()),false);
        if (!b){
            throw new BusinessException("不能修改已经启程的航班的日程安排");
        }
        crewScheduleMapper.updateById(crewSchedule);
    }

    Boolean compareFlightTime(List<String> flightNoList,boolean isDelete){
        Map<LocalDateTime, LocalDateTime> timeMap = flightFeign
                .getFlyingTimeByFlightNoList(flightNoList);
        if (isDelete)
        {
            for (LocalDateTime departure : timeMap.keySet()) {
                LocalDateTime now = LocalDateTime.now();
                if (now.isBefore(departure) && now.isBefore(timeMap.get(departure))) {
                    return false;
                }
            }
        }else
        for (LocalDateTime departure : timeMap.keySet()) {
            LocalDateTime now = LocalDateTime.now();
            System.out.println(now+"|"+departure);
            if (now.isAfter(departure)) {
                return false;
            }
        }
        return true;
    }


    @Override
    public void deleteCrewSchedule(List<Long> ids) {
        log.info("删除日程安排接口service启动了");
        List<String> flightIdList = crewScheduleMapper.selectObjs(new QueryWrapper<CrewSchedule>()
                .select("flight_id")
                .in("id", ids)
        );
        if (!compareFlightTime(flightIdList,true)){
            throw new BusinessException("不能删除已经启程的航班的日程安排");
        }
        crewScheduleMapper.deleteByIds(ids);

    }
}