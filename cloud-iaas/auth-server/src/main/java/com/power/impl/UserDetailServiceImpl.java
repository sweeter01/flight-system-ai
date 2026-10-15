package com.power.impl;

import com.power.constant.AuthConstants;
import com.power.factory.LoginStrategyFactory;
import com.power.strategy.LoginStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;


@Slf4j
@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    public LoginStrategyFactory loginStrategyFactory;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        ServletRequestAttributes requestAttributes =(ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        HttpServletRequest request = requestAttributes.getRequest();
        String header = request.getHeader(AuthConstants.LOGIN_TYPE);

        // 判断登录类型
        if(!StringUtils.hasText(header))
        {
            throw new UsernameNotFoundException("登录类型不能为空");
        }

        //从登录策略工厂获取登录对象
        LoginStrategy strategy = loginStrategyFactory.getLoginStrategy(header);
        return strategy.realStrategy(username);

    }
}
