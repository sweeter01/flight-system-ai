package com.power.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.power.constant.BusinessEnum;
import com.power.filter.TokenTranslationFilter;
import com.power.model.ResourceConstants;
import com.power.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.Filter;
import java.io.PrintWriter;
import java.util.Arrays;

import static com.power.constant.HttpConstants.*;


@Configuration
@EnableWebSecurity
@ConditionalOnClass(Filter.class)
public class ResourceServerConfig {

    @Autowired
    private TokenTranslationFilter tokenTranslationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .httpBasic(httpBasic -> httpBasic.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(tokenTranslationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler())
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(ResourceConstants.RESOURCE_ALLOW_URLS)
                        .permitAll()
                        .anyRequest().authenticated()
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
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    /*
    * 认证失败处理
    * */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, e) -> {
            response.setContentType(APPLICATION_JSON);
            response.setCharacterEncoding(UTF_8);
            Result<Object> result = Result.fail(BusinessEnum.UN_AUTHORIZATION);
            ObjectMapper objectMapper = new ObjectMapper();
            String s = objectMapper.writeValueAsString(result);
            PrintWriter writer = response.getWriter();
            writer.write(s);
            writer.close();
            writer.flush();
        };
    }
    /*
    * 访问拒绝处理
    * */

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, e) -> {
            response.setHeader("WWW-Authenticate", "");
            response.setContentType(APPLICATION_JSON);
            response.setCharacterEncoding(UTF_8);
            Result<Object> result = Result.fail(BusinessEnum.ACCESS_DENY_FAIL);
            ObjectMapper objectMapper = new ObjectMapper();
            String s = objectMapper.writeValueAsString(result);
            PrintWriter writer = response.getWriter();
            writer.write(s);
            writer.flush();
            writer.close();
        };
    }
}