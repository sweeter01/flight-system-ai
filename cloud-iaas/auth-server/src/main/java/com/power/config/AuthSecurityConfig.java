package com.power.config;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.power.constant.AuthConstants;
import com.power.constant.BusinessEnum;
import com.power.constant.HttpConstants;
import com.power.impl.UserDetailServiceImpl;
import com.power.model.LoginResult;
import com.power.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.io.PrintWriter;
import java.time.Duration;
import java.util.Arrays;
import java.util.UUID;

@Configuration
@EnableWebSecurity
public class AuthSecurityConfig   {

    @Autowired
    private UserDetailServiceImpl userDetailsService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .httpBasic(httpBasic -> httpBasic.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(AuthConstants.LOGIN_URL, AuthConstants.LOGOUT_URL, "/getUserInfo").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginProcessingUrl(AuthConstants.LOGIN_URL)
                        .successHandler(authenticationSuccessHandler())
                        .failureHandler(authenticationFailureHandler())
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl(AuthConstants.LOGOUT_URL)
                        .logoutSuccessHandler(logoutSuccessHandler()))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setHeader("WWW-Authenticate", "");
                            response.setStatus(org.springframework.http.HttpStatus.UNAUTHORIZED.value());
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("{\"code\":401,\"message\":\"未登录或登录已过期\"}");
                        })
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        CorsConfigurationSource source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        ((org.springframework.web.cors.UrlBasedCorsConfigurationSource) source).registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(provider);
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return userDetailsService;
    }


    //登录成功处理器（生成token）
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler(){

        return (request, response, authentication)->{

            // 设置响应信息
            response.setContentType(HttpConstants.APPLICATION_JSON);
            response.setCharacterEncoding(HttpConstants.UTF_8);
            //使用UUID生成token令牌
            String token= UUID.randomUUID().toString();
            String jsonString = JSONObject.toJSONString(authentication.getPrincipal());
            stringRedisTemplate.opsForValue().set(AuthConstants.LOGIN_TOKEN_PREFIX+token,jsonString,
                    Duration.ofSeconds(AuthConstants.TOKEN_TIME));

            LoginResult loginResult = new LoginResult(token, AuthConstants.TOKEN_TIME);
            Result<Object> objectResult = new Result<>();
            objectResult.setData(loginResult);

            ObjectMapper objectMapper = new ObjectMapper();
            String s = objectMapper.writeValueAsString(objectResult);
            PrintWriter writer = response.getWriter();
            writer.write(s);//响应给前端
            writer.flush();
            writer.close();

        };
    }


    //登录失败处理器
    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler(){

        return (request, response, exception)->{
            // 设置响应信息
            response.setContentType(HttpConstants.APPLICATION_JSON);
            response.setCharacterEncoding(HttpConstants.UTF_8);

            Result<String> result = new Result<>();
            result.setCode(BusinessEnum.UN_AUTHORIZATION.getCode());

            if (exception instanceof BadCredentialsException) {
                result.setMessage("用户名或密码有误");
            } else if (exception instanceof UsernameNotFoundException) {
                result.setMessage("用户名不存在");
            } else if (exception instanceof AccountExpiredException) {
                result.setMessage("帐号异常，请联系管理员");
            } else if (exception instanceof InternalAuthenticationServiceException) {
                result.setMessage(exception.getMessage());
            } else {
                result.setMessage(BusinessEnum.OPERATION_FAIL.getDesc());
            }
            ObjectMapper objectMapper = new ObjectMapper();
            String s = objectMapper.writeValueAsString(result);
            PrintWriter writer = response.getWriter();
            writer.write(s);
            writer.flush();
            writer.close();

        };
    }


    //登出成功处理器（清除token）
    @Bean
    public LogoutSuccessHandler logoutSuccessHandler(){

        return (request, response, authentication)->{

            response.setContentType(HttpConstants.APPLICATION_JSON);
            response.setCharacterEncoding(HttpConstants.UTF_8);

            String header = request.getHeader(AuthConstants.AUTHORIZATION);
            String token = header.replaceFirst(AuthConstants.BEARER, "");
            stringRedisTemplate.delete(AuthConstants.LOGIN_TOKEN_PREFIX+token);

            Result<Object> result = new Result<>();

            ObjectMapper objectMapper = new ObjectMapper();
            result.setMessage("登出成功");
            PrintWriter writer = response.getWriter();
            writer.write(objectMapper.writeValueAsString(result));
            writer.flush();
            writer.close();
        };
    }

//@Bean
//public PasswordEncoder passwordEncoder(){
//    // 生产环境不要使用这个！
//    return NoOpPasswordEncoder.getInstance(); // 明文比较
//}

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}