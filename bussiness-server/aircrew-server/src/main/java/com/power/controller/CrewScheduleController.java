package com.power.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.power.model.Result;
import com.power.pojo.CrewSchedule;
import com.power.service.CrewScheduleService;
import com.power.service.CrewService;
import com.power.vo.CrewBaseVo;
import com.power.vo.CrewScheduleVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;

@Api(tags = "机组人员航班日程安排表")
@RestController
@RequestMapping("/crewSchedule")
@Slf4j
public class CrewScheduleController {

    @Autowired
    private CrewScheduleService crewScheduleService;

    @Autowired
    private  CrewService crewService;


    @GetMapping("/list")
    @ApiOperation(value = "查询所有机组人员航班日程安排")
    @PreAuthorize("hasAuthority('crewSchedule:list')")
    public Result<Page>  getCrewScheduleList(
            @RequestParam(required = false)  String flightId,
            @RequestParam(required = false)  Long pilotId,
            @RequestParam(required = false)  Long assistantId,
            @RequestParam(required = false)  Integer status,
            @RequestParam(required = false)  Integer dutyType,
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size
    ) {

        log.info("查询所有机组人员航班日程安排，参数：{}, {}, {}, {}, {}, {}, {}", flightId, pilotId, assistantId, status, dutyType, current, size);
        Page<CrewScheduleVo> page = crewScheduleService.getInfoByPage(flightId, pilotId, assistantId, status, dutyType, current, size);
        return Result.success(page);
    }

    /*
    *   新增，修改，删除注意事项：
    *   -------------------------------
    *     X   启程前  飞行中   完成后
    *   -------------------------------
    *   新增   YES     YES     YES
    *   -------------------------------
    *   修改   YES     NO     NO
    *   -------------------------------
    *   删除   YES     NO     YES
    *   -------------------------------
    * */
    @PostMapping("/update")
    @ApiOperation(value = "更新机组人员航班日程安排")
    @PreAuthorize("hasAuthority('crewSchedule:update')")
    public Result updateCrewSchedule(@RequestBody CrewSchedule crewSchedule) {
        log.info("更新机组人员航班日程安排，参数：{}", crewSchedule);
        crewScheduleService.updateCrewSchedule(crewSchedule);
        return Result.success();
    }
    @PutMapping("/add")
    @ApiOperation(value = "添加机组人员航班日程安排")
    @PreAuthorize("hasAuthority('crewSchedule:add')")
    public Result addCrewSchedule(@RequestBody CrewSchedule crewSchedule) {
        log.info("添加机组人员航班日程安排，参数：{}", crewSchedule);
        crewScheduleService.save(crewSchedule);
        return Result.success();
    }

    @DeleteMapping("/delete/{ids}")
    @ApiOperation(value = "删除机组人员航班日程安排")
    @PreAuthorize("hasAuthority('crewSchedule:delete')")
    public Result deleteCrewSchedule(@PathVariable List<Long> ids) {
        log.info("删除机组人员航班日程安排，参数：{}", ids);
        crewScheduleService.removeByIds(ids);
        return Result.success();
    }

    /// //////////  查询航班日程安排的人员详情信息【前端对应查看查看详情按钮】  ///////////
    @GetMapping("/getDetail")
    @ApiOperation(value = "查询航班日程安排的机组人员详情信息")
    public  Result<LinkedHashMap<String,List<CrewBaseVo>>>getCrewScheduleCrewDetail(@RequestParam("ids") List<Integer> ids) {
        /*
        *ids.get(0) 为飞行员大队详情信息的pilotId
        * ids.get(1) 为乘务大队详情信息的assistantId
        * */

        log.info("查询航班日程安排的机组人员详情信息，参数：{}", ids);
        int[] idArray = ids.stream().mapToInt(Integer::intValue).toArray();
        LinkedHashMap<String, List<CrewBaseVo>> map = crewService.getCrewScheduleCrewDetail(idArray);
        return Result.success(map);
    }

}