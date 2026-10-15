package com.power.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.power.Utils.AuthUtil;
import com.power.constant.AuthConstants;
import com.power.domain.SysRole;
import com.power.model.Result;
import com.power.service.SysRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "系统角色接口管理")
@RequestMapping("sys/role")
@RestController

public class SysRoleController {

    @Autowired
    private SysRoleService sysRoleService;


    @ApiOperation("查询系统角色列表")
    @GetMapping("list")
    @PreAuthorize("hasAuthority('sys:role:list')")
    public Result<List<SysRole>> loadSysRoleList(){
        System.out.println("查询所有的系统角色");
        List<SysRole> sysRoleList = sysRoleService.querySysRoleList();
        sysRoleList.forEach(System.out::println);
        return Result.success(sysRoleList);
    }
    @ApiOperation("多条件查询系统角色列表")
    @GetMapping("page")
    @PreAuthorize("hasAuthority('sys:role:page')")
    public Result<Page<SysRole>> loadSysRoleListByPage(
            Integer current,
            Integer size,
            String roleName) {
        com.baomidou.mybatisplus.extension.plugins.pagination.
                Page<SysRole> rolePage = new Page<>(current==null?1:current, size==null?10:size);;
        Page<SysRole> paged = sysRoleService.page(rolePage, new LambdaQueryWrapper<SysRole>()
                .like(StringUtils.hasText(roleName), SysRole::getRoleName, roleName)
                .orderByDesc(SysRole::getCreateTime));
        return Result.success(paged);
    }

    @ApiOperation("新增角色")
    @PostMapping
    @PreAuthorize("hasAuthority('sys:role:save')")
    public Result<String> saveSysRole(@RequestBody SysRole sysRole,
                                      @RequestHeader(value = "Authorization", required = false) String authorization) {
        String token = authorization != null ? authorization.replaceFirst(AuthConstants.BEARER, "") : null;
        Long createUserId = AuthUtil.getLoginUserId(token);
        Boolean saved = sysRoleService.saveSysRole(sysRole, createUserId);
        return Result.handle(saved);
    }

    @ApiOperation("根据标识查询角色详情")
    @GetMapping("info/{roleId}")
    @PreAuthorize("hasAuthority('sys:role:info')")
    public Result<SysRole> loadSysRoleInfo(@PathVariable Long roleId) {
        SysRole sysRole = sysRoleService.querySysRoleInfoByRoleId(roleId);
        return Result.success(sysRole);
    }

    @ApiOperation("修改角色信息")
    @PutMapping
    @PreAuthorize("hasAuthority('sys:role:update')")
    public Result<String> modifySysRole(@RequestBody SysRole sysRole) {
        Boolean updated = sysRoleService.modifySysRole(sysRole);
        return Result.handle(updated);
    }

    @ApiOperation("批量/单个删除角色")
    @DeleteMapping
    @PreAuthorize("hasAuthority('sys:role:delete')")
    public Result<String> removeSysRole(@RequestBody List<Long> roleIdList) {
        Boolean removed = sysRoleService.removeSysRoleListByIds(roleIdList);
        return Result.handle(removed);
    }

}