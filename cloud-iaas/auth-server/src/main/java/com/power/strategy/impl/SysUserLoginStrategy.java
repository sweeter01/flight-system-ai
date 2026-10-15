package com.power.strategy.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.power.constant.AuthConstants;
import com.power.domain.LoginSysUser;
import com.power.mapper.LoginSysUserMapper;
import com.power.model.SecurityUser;
import com.power.strategy.LoginStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service(AuthConstants.SYS_USER_LOGIN)
public class SysUserLoginStrategy implements LoginStrategy {

    @Autowired
    private LoginSysUserMapper loginSysUserMapper;

    @Override
    public UserDetails realStrategy(String username) {
        LoginSysUser loginSysUser = loginSysUserMapper.selectOne(
                new LambdaQueryWrapper<LoginSysUser>()
                        .eq(LoginSysUser::getUsername, username)
        );

        if (ObjectUtil.isEmpty(loginSysUser)) {
            throw new UsernameNotFoundException("账号或密码错误！");
        }

        SecurityUser securityUser = new SecurityUser();
        Set<String> perms = loginSysUserMapper.selectPermsByUserId(loginSysUser.getUserId());
        securityUser.setUserId(loginSysUser.getUserId());
        securityUser.setUsername(loginSysUser.getUsername());
        securityUser.setPassword(loginSysUser.getPassword());
        securityUser.setLoginType(AuthConstants.SYS_USER_LOGIN);
        securityUser.setStatus(loginSysUser.getStatus());

        if (!ObjectUtil.isEmpty(perms) && !perms.isEmpty()) {
            securityUser.setPerms(perms);
        }
        return securityUser;
    }
}
