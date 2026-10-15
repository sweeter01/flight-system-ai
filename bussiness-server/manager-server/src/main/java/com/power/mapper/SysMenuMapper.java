package com.power.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.power.domain.SysMenu;
import org.apache.ibatis.annotations.Mapper;

import java.util.Set;

@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    /**
     * 根据用户标识查询菜单权限集合
     * @return
     */
    Set<SysMenu> selectUserMenuListByUserId(Long userId);
}