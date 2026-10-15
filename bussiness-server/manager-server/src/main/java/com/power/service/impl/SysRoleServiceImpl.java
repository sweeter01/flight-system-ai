package com.power.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.power.constant.ManagerConstant;
import com.power.domain.SysRole;
import com.power.domain.SysRoleMenu;
import com.power.mapper.SysRoleMapper;
import com.power.mapper.SysRoleMenuMapper;
import com.power.service.SysRoleMenuService;
import com.power.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Service
@CacheConfig(cacheNames = "com.power.service.impl.SysRoleServiceImpl")
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {


    @Autowired
    private  SysRoleMapper sysRoleMapper;

    @Autowired
    private SysRoleMenuService sysRoleMenuService;
    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Override
    @Cacheable(key = ManagerConstant.ALL_ROLES)
    public List<SysRole> querySysRoleList() {
        System.out.println("查询所有的系统角色service");
        return sysRoleMapper.selectList(new LambdaQueryWrapper<SysRole>()
                .orderByDesc(SysRole::getCreateTime));

    }

    @Override
    @CacheEvict(key = ManagerConstant.ALL_ROLES)
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveSysRole(SysRole sysRole, Long createUserId) {

        sysRole.setCreateTime(new Date(System.currentTimeMillis()));
        sysRole.setCreateUserId(createUserId);
        int inserted = sysRoleMapper.insert(sysRole);
        if (inserted>0)
        {
            addNewRoleMenu(sysRole);
        }
        return inserted>0;
    }



    //添加角色权限关系的函数
    private void addNewRoleMenu(SysRole sysRole) {
        Long roleId = sysRole.getRoleId();
        List<Long> menuIdList = sysRole.getMenuIdList();
        ArrayList<SysRoleMenu> sysRoleMenus = new ArrayList<>();
        for (Long menuId : menuIdList) {
            sysRoleMenus.add(new SysRoleMenu(null,roleId,menuId));
        }

        sysRoleMenuService.saveBatch(sysRoleMenus);
    }

    @Override
    public SysRole querySysRoleInfoByRoleId(Long roleId) {
        SysRole sysRole = sysRoleMapper.selectById(roleId);
        // 根据角色标识查询角色与权限关系集合
        List<SysRoleMenu> roleMenus = sysRoleMenuMapper.selectList(new LambdaQueryWrapper<SysRoleMenu>()
                .eq(SysRoleMenu::getRoleId, roleId));

        if(!CollectionUtils.isEmpty(roleMenus) && roleMenus.size()>0)
        {
            List<Long> menuIdList = roleMenus.stream()
                    .map(SysRoleMenu::getMenuId)
                    .toList();
            sysRole.setMenuIdList(menuIdList);
        }
        return sysRole;
    }

    @Override
    public Boolean modifySysRole(SysRole sysRole) {

        Long roleId = sysRole.getRoleId();

        sysRoleMenuService.remove(new LambdaQueryWrapper<SysRoleMenu>()
                .eq(SysRoleMenu::getRoleId, roleId));//删除旧的角色权限关系

        addNewRoleMenu(sysRole);//添加角色权限关系

        return sysRoleMapper.updateById(sysRole)>0;
    }

    @Override
    public Boolean removeSysRoleListByIds(List<Long> roleIdList) {
        sysRoleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>()
                .in(SysRoleMenu::getRoleId,roleIdList));
        return sysRoleMapper.deleteBatchIds(roleIdList)==roleIdList.size();
    }
}