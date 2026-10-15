package com.power.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.power.Utils.AuthUtil;
import com.power.constant.AuthConstants;
import com.power.domain.SysUser;
import com.power.domain.SysUserRole;
import com.power.mapper.SysUserMapper;
import com.power.mapper.SysUserRoleMapper;
import com.power.service.SysUserRoleService;
import com.power.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Autowired
    private  SysUserMapper sysUserMapper;

    @Autowired
    private SysUserRoleService sysUserRoleService;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private  PasswordEncoder passwordEncoder;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer saveSysUser(SysUser sysUser, Long createUserId) {
          sysUser.setCreateUserId(createUserId);
          sysUser.setCreateTime(new Date(System.currentTimeMillis()));
          sysUser.setPassword(passwordEncoder.encode(sysUser.getPassword()));
        int res = sysUserMapper.insert(sysUser);

        if(res>0)
        {
            addUserInfo(sysUser, sysUser.getUserId());
        }
        return res;
    }

    @Override
    public SysUser querySysUserInfoByUserId(Long id) {

        SysUser sysUser = sysUserMapper.selectById(id);

        List<SysUserRole> sysUserRoleList = sysUserRoleMapper.selectList(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, id));

        if(!CollectionUtils.isEmpty(sysUserRoleList))
        {
            List<Long> collected = sysUserRoleList.stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
            sysUser.setRoleIdList(collected);
        }
        return  sysUser;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer modifySysUserInfo(SysUser sysUser) {

        Long userId = sysUser.getUserId();
         sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, userId));
            //删除角色信息成功
            addUserInfo(sysUser, userId); //添加修改的角色信息（roleId）
        if (StringUtils.hasText(sysUser.getPassword())) {
            sysUser.setPassword(passwordEncoder.encode(sysUser.getPassword()));
        }
        return sysUserMapper.updateById(sysUser);
    }

    @Override
    public Boolean removeSysUserListByUserIds(List<Long> userIds) {

        sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>()
                .in(SysUserRole::getUserId, userIds));
        return sysUserMapper.deleteBatchIds(userIds)==userIds.size();
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer modifyPersonalInfo(SysUser sysUser, String token) {
        if (StringUtils.hasText(sysUser.getPassword())) {
            sysUser.setPassword(passwordEncoder.encode(sysUser.getPassword()));
        } else {
            sysUser.setPassword(null);
        }
        int updated = sysUserMapper.updateById(sysUser); // 更新用户信息
        //如果更新成功，且有token，删除登录管理员的token
        if (updated > 0 && StringUtils.hasText(token)) {
            Boolean deleted = stringRedisTemplate.delete(AuthConstants.LOGIN_TOKEN_PREFIX + token);
            if (deleted == null || !deleted) {
                throw new RuntimeException("删除Redis token失败，个人信息修改已回滚");
            }
        }
        return updated;
    }



    private void addUserInfo(SysUser sysUser, Long userId) {
        List<Long> roleIdList = sysUser.getRoleIdList();
        ArrayList<SysUserRole> list = new ArrayList<>();
        roleIdList.forEach(roleId -> {
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setUserId(userId);
            sysUserRole.setRoleId(roleId);
            list.add(sysUserRole);
        });
        list.forEach(System.out::println);
        sysUserRoleService.saveBatch(list); //第二次数据库操作
    }

}