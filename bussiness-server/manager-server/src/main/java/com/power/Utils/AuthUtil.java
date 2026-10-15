package com.power.Utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.power.constant.AuthConstants;
import com.power.model.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Set;

@Component
public class AuthUtil {

    private static StringRedisTemplate stringRedisTemplate;
    private static ObjectMapper objectMapper;

    @Autowired
    public void setDependencies(StringRedisTemplate stringRedisTemplate, ObjectMapper objectMapper) {
        AuthUtil.stringRedisTemplate = stringRedisTemplate;
        AuthUtil.objectMapper = objectMapper;
    }

    public static SecurityUser getLoginUser(String token){
        if (!StringUtils.hasText(token)) {
            throw new RuntimeException("用户未登录");
        }
        String userStr = stringRedisTemplate.opsForValue().get(AuthConstants.LOGIN_TOKEN_PREFIX + token);
        if (!StringUtils.hasText(userStr)) {
            throw new RuntimeException("登录已过期，请重新登录");
        }
        try {
            return objectMapper.readValue(userStr, SecurityUser.class);
        } catch (Exception e) {
            throw new RuntimeException("解析用户信息失败");
        }
    }

    public static SecurityUser getLoginUser(){
        throw new UnsupportedOperationException("WebFlux环境中请使用 getLoginUser(String token) 方法");
    }

    public static Long getLoginUserId(String token){
        return getLoginUser(token).getUserId();
    }

    public static Set<String> getLoginUserPerms(String token){
        return getLoginUser(token).getPerms();
    }
}