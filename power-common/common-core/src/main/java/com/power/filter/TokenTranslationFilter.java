package com.power.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.power.constant.AuthConstants;
import com.power.model.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class TokenTranslationFilter extends OncePerRequestFilter {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 获取请求头中的Authorization值
        String authorization = request.getHeader(AuthConstants.AUTHORIZATION);
        // 判断是否有值
        if (StringUtils.hasText(authorization)) {
            // 获取token值
            String token = authorization.replaceFirst(AuthConstants.BEARER, "");
            // 判断token是否有值
            if (StringUtils.hasText(token)) {
                // 处理token续约
                // 从redis中获取token的有效时长
                Long expire = stringRedisTemplate.getExpire(AuthConstants.LOGIN_TOKEN_PREFIX + token, TimeUnit.SECONDS);
                // 判断是否小于过期阈值
                if (expire < AuthConstants.TOKEN_EXPIRE_THRESHOLD_TIME) {
                    // token续约
                    stringRedisTemplate.expire(AuthConstants.LOGIN_TOKEN_PREFIX + token, Duration.ofSeconds(AuthConstants.TOKEN_TIME));
                }

                // token转换为用户信息
                // 从redis中获取用户信息
                String userStr = stringRedisTemplate.opsForValue().get(AuthConstants.LOGIN_TOKEN_PREFIX + token);
                // 将 json 格式字符串的用户信息转换为 SecurityUser 对象
                SecurityUser securityUser = objectMapper.readValue(userStr, SecurityUser.class);
                // 处理权限
                Set<SimpleGrantedAuthority> grantedAuthorities = securityUser.getPerms()
                        .stream().map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toSet());
                // 创建 security 框架认识的用户对象
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(securityUser,null,grantedAuthorities);
                // 将 security 框架认识的用户对象存放到 security 上下文中
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        filterChain.doFilter(request,response);
    }
}
