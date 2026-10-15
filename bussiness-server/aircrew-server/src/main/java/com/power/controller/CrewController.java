package com.power.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.power.enums.CrewPosition;
import com.power.model.Result;
import com.power.pojo.Crew;
import com.power.service.CrewService;
import com.power.vo.CrewVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Slf4j
@Api("机组人员信息")
@RequestMapping("/crew")
public class CrewController {

    @Autowired
    private CrewService crewService;

    @GetMapping("/listCrew")
    @ApiOperation(value = "分页查询机组人员信息", notes = "根据姓名、性别、机组编号分页查询机组人员信息")
    @PreAuthorize("hasAuthority('crew:listCrew')")
    public Result<Page> listCrewByPage(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer gender,
            @RequestParam(required = false) String crewNo,
            @RequestParam(required = false) String positionType,
            @RequestParam(required = false) Long groupId,
            @RequestParam(required = false) Long baseCityId,
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size) {

        log.info("开始分页查询机组人员信息 - 参数: name={}, gender={}, crewNo={}, positionType={}, groupId={}, baseCityId={}, current={}, size={}",
                name, gender, crewNo, positionType, groupId, baseCityId, current, size);

        Page<CrewVo> crewVoPage = crewService.listCrewByPage(name, gender, crewNo, positionType, groupId, baseCityId, current, size);
        return Result.success(crewVoPage);
    }

    @PutMapping("/updateCrew")
    @ApiOperation(value = "更新机组人员信息", notes = "根据机组人员ID更新机组人员信息")
    @PreAuthorize("hasAuthority('crew:updateCrew')")
    public Result updateCrew(@RequestBody Crew crew) {
        log.info("开始更新机组人员信息 - 参数: {}", crew);
        crewService.updateCrew(crew);
        return Result.success();
    }
    @PostMapping("/addCrew")
    @ApiOperation(value = "添加机组人员信息", notes = "添加机组人员信息")
    @PreAuthorize("hasAuthority('crew:addCrew')")
    public Result addCrew(@RequestBody Crew crew) {
        log.info("开始添加机组人员信息 - 参数: {}", crew);
        crewService.assignCrewToFlight(crew);
        return Result.success();
    }
    @DeleteMapping("/deleteCrew/{ids}")
    @ApiOperation(value = "删除机组人员信息", notes = "根据机组人员ID删除机组人员信息")
    @PreAuthorize("hasAuthority('crew:deleteCrew')")
    public Result deleteCrew(@PathVariable("ids") List<Long> ids) {
        log.info("开始删除机组人员信息 - 参数: {}", ids);
        crewService.deleteCrew(ids);
        return Result.success();
    }

    ///////////////  查询机长或乘务长的id和姓名  ///////////////////

    @GetMapping("/crewListByPosition")
    @ApiOperation(value = "查询所有机长或乘务长的id和姓名")
    public Result<Map<Long,String>> getCrewListByPosition(String  columnName) {
        log.info("开始查询所有机长或乘务长的id和姓名 - 参数: columnName={}", columnName);
        Map<Long, String> result = crewService.list(new QueryWrapper<Crew>()
                                .select("id", "name")
                                .eq("position_type", columnName.equals(CrewPosition.PILOT.getDescription())?
                                        CrewPosition.PILOT:
                                        CrewPosition.FLIGHT_ATTENDANT_LEADER)
                        )
                        .stream()
                        .collect(Collectors.toMap(Crew::getId, Crew::getName));
        return Result.success(result);
    }
}