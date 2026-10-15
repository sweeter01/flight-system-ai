package com.power.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Configuration
public class GatewaySecurityConfig {


    /*
    * 安全配置
    * 禁用CSRF保护
    * 禁用HTTP Basic认证
    * 禁用表单登录
    * */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .cors(cors -> cors.disable())
                .csrf(csrf -> csrf.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(formLogin -> formLogin.disable())
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(serverAuthenticationEntryPoint())
                        .accessDeniedHandler(serverAccessDeniedHandler())
                )
                .authorizeExchange(exchange -> exchange.anyExchange().permitAll())
                .build();
    }

    /**
     * 跨域配置
     * 允许所有来源、方法、头
     * 允许携带凭证
     * 最大缓存时间3600秒
     */


    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(Arrays.asList("*"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsWebFilter(source);
    }


    /**
     * 认证入口点
     * 当用户未登录或登录已过期时，会调用该处理程序
     */
    @Bean
    public ServerAuthenticationEntryPoint serverAuthenticationEntryPoint() {
        return (exchange, ex) -> {
            var response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            response.getHeaders().set("Content-Type", "application/json;charset=UTF-8");
            response.getHeaders().remove("WWW-Authenticate");
            var buffer = response.bufferFactory().wrap("{\"code\":401,\"message\":\"未登录或登录已过期\"}".getBytes());
            return response.writeWith(Mono.just(buffer));
        };
    }

    /*
    * 访问被拒绝处理
    * 当用户没有权限访问某个资源时，会调用该处理程序
    * */
    @Bean
    public ServerAccessDeniedHandler serverAccessDeniedHandler() {
        return (exchange, denied) -> {
            var response = exchange.getResponse();
            response.setStatusCode(HttpStatus.FORBIDDEN);
            response.getHeaders().set("Content-Type", "application/json;charset=UTF-8");
            response.getHeaders().remove("WWW-Authenticate");
            var buffer = response.bufferFactory().wrap("{\"code\":403,\"message\":\"访问被拒绝\"}".getBytes());
            return response.writeWith(Mono.just(buffer));
        };
    }
}