package com.power.service;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.power.domain.SysLog;
import com.power.domain.SysMenu;
import com.power.domain.SysRole;
import com.power.domain.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
@Slf4j
public class ManagerAiTools {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private SysMenuService sysMenuService;

    @Autowired
    private SysLogService sysLogService;

    @Tool(description = "根据用户名查询系统管理员列表，最多返回100条记录")
    public List<SysUser> queryUserList(
            @ToolParam(description = "用户名，可为空，支持模糊查询", required = false) String username) {
        log.info("AI工具-查询管理员列表: username={}", username);
        return sysUserService.list(new LambdaQueryWrapper<SysUser>()
                .like(StringUtils.hasText(username), SysUser::getUsername, username)
                .orderByDesc(SysUser::getCreateTime)
                .last("limit 100"));
    }

    @Tool(description = "根据用户ID查询管理员详细信息及其角色，当用户想查看某个管理员详情时使用此工具")
    public SysUser queryUserInfoById(
            @ToolParam(description = "用户ID，不为空") Long id) {
        log.info("AI工具-查询管理员详情: id={}", id);
        return sysUserService.querySysUserInfoByUserId(id);
    }

    @Tool(description = "修改管理员信息，当用户要求修改、更新管理员信息时使用此工具")
    public String modifySysUserInfo(SysUser sysUser) {
        log.info("AI工具-修改管理员信息: {}", sysUser);
        Integer modify = sysUserService.modifySysUserInfo(sysUser);
        return modify > 0 ? "管理员信息修改成功" : "管理员信息修改失败";
    }

    @Tool(description = "删除管理员，当用户要求删除、移除管理员时使用此工具，支持批量删除")
    public String removeSysUser(
            @ToolParam(description = "用户ID集合，不为空") List<Long> userIds) {
        log.info("AI工具-删除管理员: userIds={}", userIds);
        Boolean res = sysUserService.removeSysUserListByUserIds(userIds);
        return res ? "管理员删除成功" : "管理员删除失败";
    }

    @Tool(description = "查询所有系统角色列表，当用户想查看所有角色时使用此工具，最多返回100条记录")
    public List<SysRole> queryRoleList() {
        log.info("AI工具-查询所有角色列表");
        return sysRoleService.list(new LambdaQueryWrapper<SysRole>()
                .orderByDesc(SysRole::getCreateTime)
                .last("limit 100"));
    }

    @Tool(description = "根据角色名称查询系统角色列表，最多返回100条记录")
    public List<SysRole> queryRoleListByName(
            @ToolParam(description = "角色名称，可为空，支持模糊查询", required = false) String roleName) {
        log.info("AI工具-查询角色列表: roleName={}", roleName);
        return sysRoleService.list(new LambdaQueryWrapper<SysRole>()
                .like(StringUtils.hasText(roleName), SysRole::getRoleName, roleName)
                .orderByDesc(SysRole::getCreateTime)
                .last("limit 100"));
    }

    @Tool(description = "根据角色ID查询角色详细信息，当用户想查看某个角色详情时使用此工具")
    public SysRole queryRoleInfo(
            @ToolParam(description = "角色ID，不为空") Long roleId) {
        log.info("AI工具-查询角色详情: roleId={}", roleId);
        return sysRoleService.querySysRoleInfoByRoleId(roleId);
    }

    @Tool(description = "修改角色信息，当用户要求修改、更新角色信息时使用此工具")
    public String modifySysRole(SysRole sysRole) {
        log.info("AI工具-修改角色信息: {}", sysRole);
        Boolean updated = sysRoleService.modifySysRole(sysRole);
        return updated ? "角色信息修改成功" : "角色信息修改失败";
    }

    @Tool(description = "删除角色，当用户要求删除、移除角色时使用此工具，支持批量删除")
    public String removeSysRole(
            @ToolParam(description = "角色ID集合，不为空") List<Long> roleIdList) {
        log.info("AI工具-删除角色: roleIdList={}", roleIdList);
        Boolean removed = sysRoleService.removeSysRoleListByIds(roleIdList);
        return removed ? "角色删除成功" : "角色删除失败";
    }

    @Tool(description = "查询系统所有菜单权限集合，当用户想查看权限列表、菜单树时使用此工具，最多返回100条记录")
    public List<SysMenu> queryAllMenuList() {
        log.info("AI工具-查询所有菜单权限集合");
        return sysMenuService.list(new LambdaQueryWrapper<SysMenu>()
                .last("limit 100"));
    }

    @Tool(description = "新增菜单权限，当用户要求添加、创建权限或菜单时使用此工具")
    public String saveSysMenu(SysMenu sysMenu) {
        log.info("AI工具-新增菜单权限: {}", sysMenu);
        Boolean saved = sysMenuService.saveSysMenu(sysMenu);
        return saved ? "菜单权限新增成功" : "菜单权限新增失败";
    }

    @Tool(description = "根据菜单ID查询权限详细信息，当用户想查看某个权限详情时使用此工具")
    public SysMenu queryMenuInfo(
            @ToolParam(description = "菜单权限ID，不为空") Long menuId) {
        log.info("AI工具-查询菜单权限详情: menuId={}", menuId);
        return sysMenuService.getById(menuId);
    }

    @Tool(description = "修改菜单权限信息，当用户要求修改、更新权限或菜单信息时使用此工具")
    public String modifySysMenu(SysMenu sysMenu) {
        log.info("AI工具-修改菜单权限信息: {}", sysMenu);
        Boolean modified = sysMenuService.modifySysMenu(sysMenu);
        return modified ? "菜单权限修改成功" : "菜单权限修改失败";
    }

    @Tool(description = "删除菜单权限，当用户要求删除、移除权限或菜单时使用此工具")
    public String removeSysMenu(
            @ToolParam(description = "菜单权限ID，不为空") Long menuId) {
        log.info("AI工具-删除菜单权限: menuId={}", menuId);
        Boolean removed = sysMenuService.removeSysMenuById(menuId);
        return removed ? "菜单权限删除成功" : "菜单权限删除失败";
    }

    @Tool(description = "查询系统操作日志，最多返回100条记录，仅支持查询不可修改")
    public List<SysLog> querySysLogList(
            @ToolParam(description = "用户ID，可为空", required = false) Long userId,
            @ToolParam(description = "操作描述，可为空，支持模糊查询", required = false) String operation) {
        log.info("AI工具-查询系统操作日志: userId={}, operation={}", userId, operation);
        return sysLogService.list(new LambdaQueryWrapper<SysLog>()
                .eq(ObjectUtil.isNotNull(userId), SysLog::getUserId, userId)
                .like(StringUtils.hasText(operation), SysLog::getOperation, operation)
                .orderByDesc(SysLog::getCreateDate)
                .last("limit 100"));
    }
}