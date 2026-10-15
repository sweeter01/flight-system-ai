package com.power.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.power.domain.SysRole;

import java.util.List;


public interface SysRoleService extends IService<SysRole>{


    /**
     * 查询系统所有角色
     * @return
     */
    List<SysRole> querySysRoleList();

    /**
     * 新增角色
     * @param sysRole
     * @param createUserId 创建用户ID
     * @return
     */
    Boolean saveSysRole(SysRole sysRole, Long createUserId);

    /**
     * 根据标识查询角色详情
     * @param roleId
     * @return
     */
    SysRole querySysRoleInfoByRoleId(Long roleId);

    /**
     * 修改角色信息
     * @param sysRole
     * @return
     */
    Boolean modifySysRole(SysRole sysRole);

    /**
     * 批量/单个删除角色
     * @param roleIdList
     * @return
     */
    Boolean removeSysRoleListByIds(List<Long> roleIdList);
}