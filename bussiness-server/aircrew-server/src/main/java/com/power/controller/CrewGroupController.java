package com.power.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.power.enums.CrewPosition;
import com.power.model.Result;
import com.power.pojo.Crew;
import com.power.pojo.CrewGroup;
import com.power.service.CrewGroupService;
import com.power.service.CrewService;
import com.power.vo.CrewGroupVo;
import com.power.vo.CrewVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/crewGroup")
@Slf4j
@Api(tags = "机组小队分组信息")
public class CrewGroupController {

    @Autowired
    private CrewGroupService crewCroupService;
    @Autowired
    private CrewService crewService;

    @GetMapping("/list")
    @ApiOperation(value = "分页查询机组小队组成信息", notes = "根据小队名称、编码、状态、队长、基地城市分页查询机组小队组成信息")
    @PreAuthorize("hasAuthority('crewGroup:list')")
    public Result<Page<CrewGroupVo>> getCrewGroupList(
            @RequestParam(required = false) String groupName,
            @RequestParam(required = false) String groupCode,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long leaderId,
            @RequestParam(required = false) Long baseCityId,
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size) {

        log.info("开始分页查询机组小队组成信息 - 参数: groupName={}, groupCode={}, status={}, leaderId={}, baseCityId={}, current={}, size={}",
                groupName, groupCode, status, leaderId, baseCityId, current, size);
        return Result.success(crewCroupService.getCrewGroupPage(groupName, groupCode, status, leaderId, baseCityId, current, size));
    }

    @PutMapping("/update")
    @ApiOperation(value = "更新机组小队组成信息", notes = "根据ID更新机组小队组成信息")
    @PreAuthorize("hasAuthority('crewGroup:update')")
    public Result<CrewGroup> updateCrewGroup(@RequestBody CrewGroup crewGroup) {
        log.info("开始更新机组小队组成信息 - 参数: {}", crewGroup);
        crewCroupService.updateById(crewGroup);
        return Result.success();
    }

    @PostMapping("/add")
    @ApiOperation(value = "添加机组小队组成信息", notes = "添加机组小队组成信息")
    @PreAuthorize("hasAuthority('crewGroup:add')")
    public Result<CrewGroup> addCrewGroup(@RequestBody CrewGroup crewGroup) {
        log.info("开始添加机组小队组成信息 - 参数: {}", crewGroup);
        crewCroupService.save(crewGroup);
        return Result.success(crewGroup);
    }

    @DeleteMapping("/delete")
    @ApiOperation(value = "删除机组小队组成信息", notes = "根据ID删除机组小队组成信息")
    @PreAuthorize("hasAuthority('crewGroup:delete')")
    public Result deleteCrewGroup(@RequestParam("id") Long id) {
        log.info("开始删除机组小队组成信息 - 参数: {}", id);
        crewCroupService.deleteCrewGroup(id);
        return Result.success();
    }


    ///////////////   机组小队组成信息 【新增/编辑对话框中】  ///////////////////

    @GetMapping("/cityDetail")
    @ApiOperation(value = "查询可选基地城市信息")
    public Result<Map<Long,String>> getCityMap() {
        log.info("开始查询所有在运营中的基地城市信息");
        return Result.success(crewService.getCityIdAndName());
    }

    @GetMapping("/crewList")
    @ApiOperation(value = "查询所有机组人员id和姓名")
    public Result<Map<Long,String>> getCrewList() {
        log.info("开始查询所有机组人员id和姓名");
        Map<Long, String> result = crewService.list(new QueryWrapper<Crew>()
                                .select("id", "name"))
                        .stream()
                        .collect(Collectors.toMap(Crew::getId, Crew::getName));
        return Result.success(result);
    }

    @GetMapping("/groupDetailById")
    @ApiOperation(value = "查询机组小队的人员列表", notes = "根据小队ID查询机组小队的人员列表")
    public Result<Map<Long,String>> getCrewGroupMap(@RequestParam("id") Long id) {
        log.info("开始查询机组小队组成信息 - 参数: id={}", id);
        Map<Long, String> result = crewService.list(new QueryWrapper<Crew>()
                                .select("id","name")
                                .eq("group_id",id))
                        .stream()
                        .collect(Collectors.toMap(Crew::getId, Crew::getName));
        return Result.success(result);
    }
    @ApiOperation(value = "查询机组小队的人员详情信息", notes = "根据小队ID查询机组小队的人员详情信息")
    public  Result<List<Crew>> getGroupMemberDetail(@RequestParam("id") Long id)
    {
        log.info("开始查询机组小队的人员详情信息 - 参数: id={}", id);
        List<Crew> crewGroupList = crewService.list(new LambdaQueryWrapper<Crew>()
                .eq(Crew::getGroupId, id));
        return Result.success(crewGroupList);
    }



}