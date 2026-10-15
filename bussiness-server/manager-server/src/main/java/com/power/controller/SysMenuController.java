package com.power.controller;

import com.power.Utils.AuthUtil;
import com.power.constant.AuthConstants;
import com.power.domain.SysMenu;
import com.power.model.Result;
import com.power.service.SysMenuService;
import com.power.vo.MenuAndAuthVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Api(tags = "系统菜单权限接口管理")
@RequestMapping("/sys/menu")
@RestController
public class SysMenuController {


    @Autowired
    private  SysMenuService sysMenuService;


    @ApiOperation("查询登录用户的菜单和权限")
    @GetMapping("nav")
    public Result<MenuAndAuthVo> loadUserAuthPerms(@RequestHeader(value = "Authorization", required = false) String authorization){
        System.out.println("启动系统权限");
        String token = authorization != null ? authorization.replaceFirst(AuthConstants.BEARER, "") : null;
        Long userId = AuthUtil.getLoginUserId(token);
        Set<String> userPerms = AuthUtil.getLoginUserPerms(token);
        Set<SysMenu> sysMenus = sysMenuService.queryUserMenuListByUserId(userId);
        MenuAndAuthVo authVo = new MenuAndAuthVo(sysMenus, userPerms);
        return Result.success(authVo);
    }

    @ApiOperation("查询系统所有权限集合")
    @GetMapping("table")
    @PreAuthorize("hasAuthority('sys:menu:list')")
    public Result<List<SysMenu>> loadAllSysMenuList() {
        List<SysMenu> list = sysMenuService.queryAllSysMenuList();
        return Result.success(list);
    }

    @ApiOperation("新增权限")
    @PostMapping
    @PreAuthorize("hasAuthority('sys:menu:save')")
    public Result<String> saveSysMenu(@RequestBody SysMenu sysMenu) {
        Boolean saved = sysMenuService.saveSysMenu(sysMenu);
        return Result.handle(saved);
    }

    @ApiOperation("根据标识查询菜单权限信息")
    @GetMapping("info/{menuId}")
    @PreAuthorize("hasAuthority('sys:menu:info')")
    public Result<SysMenu> loadSysMenuInfo(@PathVariable Long menuId) {
        SysMenu sysMenu = sysMenuService.getById(menuId);
        return Result.success(sysMenu);
    }

    @ApiOperation("修改菜单权限信息")
    @PutMapping
    @PreAuthorize("hasAuthority('sys:menu:update')")
    public Result<String> modifySysMenu(@RequestBody SysMenu sysMenu) {
        Boolean modified = sysMenuService.modifySysMenu(sysMenu);
        return Result.handle(modified);
    }

    @ApiOperation("删除菜单权限")
    @DeleteMapping("{menuId}")
    @PreAuthorize("hasAuthority('sys:menu:delete')")
    public Result<String> removeSysMenu(@PathVariable Long menuId) {
        Boolean removed = sysMenuService.removeSysMenuById(menuId);
        return Result.handle(removed);
    }



}