package com.power.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.power.Utils.AuthUtil;
import com.power.constant.AuthConstants;
import com.power.constant.BusinessEnum;
import com.power.domain.SysUser;
import com.power.model.Result;
import com.power.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Api(tags = "管理员接口管理")
@RequestMapping("sys/user")
@RestController
public class  SysUserController {

    @Autowired
    private SysUserService sysUserService;



    @ApiOperation("根据用户ID查询用户信息")
    @GetMapping("info")

    public  Result<SysUser> loadSysUserInfo(@RequestHeader(value = "Authorization", required = false) String authorization){
        String token = authorization != null ? authorization.replaceFirst(AuthConstants.BEARER, "") : null;
        SysUser sysUser = sysUserService.getById(AuthUtil.getLoginUserId(token));
        return Result.success(sysUser);
    }

    @ApiOperation("多条件分页查询管理员列表")
    @GetMapping("page")
    @PreAuthorize("hasAuthority('sys:user:page')")
    public Result<Page<SysUser>> loadUserPage(
            Integer current,
            Integer size,
            String username)
    {

        log.info("多条件分页查询管理员列表: {},{}", current, size);
        Page<SysUser> userPage = new Page<>(current == null ? 1 : current, size == null ? 10 : size);
        Page<SysUser> paged = sysUserService.page(userPage, new LambdaQueryWrapper<SysUser>()
                .like(StringUtils.hasText(username), SysUser::getUsername, username)
                .orderByDesc(SysUser::getCreateTime));
        return Result.success(paged);
    }
    @ApiOperation("新增管理员")
    @PostMapping("save")
    @PreAuthorize("hasAuthority('sys:user:save')")
    public Result<String> saveSysUser(@RequestBody SysUser sysUser,
                                           @RequestHeader(value = "Authorization", required = false) String authorization){
        String token = authorization != null ? authorization.replaceFirst(AuthConstants.BEARER, "") : null;
        Long createUserId = AuthUtil.getLoginUserId(token);
        Integer save = sysUserService.saveSysUser(sysUser, createUserId);
        return Result.handle(save>0);
    }

    @ApiOperation("根据标识查询系统管理员角色")
    @GetMapping("info/{id}")
    @PreAuthorize("hasAuthority('sys:user:info')")
    public Result<SysUser> loadSysUserInfoById(@PathVariable Long id){
        SysUser sysUser = sysUserService.querySysUserInfoByUserId(id);
        return Result.success(sysUser);
    }

    @ApiOperation("修改管理员信息")
    @PutMapping("update")
    @PreAuthorize("hasAuthority('sys:user:update')")
    public Result<String> modifySysUserInfo(@RequestBody SysUser sysUser){
        Integer modify = sysUserService.modifySysUserInfo(sysUser);
        return Result.handle(modify>0);
    }

    @ApiOperation("批量/单个删除管理员")
    @DeleteMapping("{userId}")
    @PreAuthorize("hasAuthority('sys:user:delete')")
    public Result<String> removeSysUserListByUserIds(@PathVariable List<Long> userId){
        System.out.println("批量/单个删除管理员");
        Boolean res =sysUserService.removeSysUserListByUserIds(userId);
        return Result.handle(res);
    }
 /// ////////////  修改登录管理员个人信息 /////////////

    @ApiOperation("修改登录管理员个人信息")
    @PutMapping("person")
    public Result<String> modifyPersonInfo(@RequestBody SysUser sysUser,
                                                 @RequestHeader(value = "Authorization", required = false) String authorization) {
        String token = authorization != null ? authorization.replaceFirst(AuthConstants.BEARER, "") : null;
        Long userId = AuthUtil.getLoginUserId(token);
        sysUser.setUserId(userId);
        Integer i = sysUserService.modifyPersonalInfo(sysUser,token);
        return i>0 ? Result.success("个人信息修改成功") : Result.fail(BusinessEnum.OPERATION_FAIL, "个人信息修改失败");
    }

}