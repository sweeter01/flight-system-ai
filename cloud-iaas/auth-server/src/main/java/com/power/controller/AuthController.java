package com.power.controller;

import com.alibaba.fastjson.JSONObject;
import com.power.constant.AuthConstants;
import com.power.constant.BusinessEnum;
import com.power.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping
public class AuthController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping("/getUserInfo")
    public Result<Map<String, Object>> getUserInfo(
            @RequestHeader(value = AuthConstants.AUTHORIZATION, required = false) String authorization) {

        Map<String, Object> result = new HashMap<>();

        if (authorization == null || !authorization.startsWith(AuthConstants.BEARER)) {
            return Result.fail(BusinessEnum.UN_AUTHORIZATION, "未登录");
        }

        String token = authorization.replaceFirst(AuthConstants.BEARER, "");
        String userInfo = stringRedisTemplate.opsForValue().get(AuthConstants.LOGIN_TOKEN_PREFIX + token);

        if (userInfo == null) {
            return Result.fail(BusinessEnum.UN_AUTHORIZATION, "登录已过期，请重新登录");
        }

        JSONObject jsonObject = JSONObject.parseObject(userInfo);
        result.put("userId", jsonObject.getLong("userId"));
        result.put("username", jsonObject.getString("username"));
        result.put("perms", jsonObject.getJSONArray("perms"));
        result.put("loginType", jsonObject.getString("loginType"));

        return Result.success(result);
    }
}