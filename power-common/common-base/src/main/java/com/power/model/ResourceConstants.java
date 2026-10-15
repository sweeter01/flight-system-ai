package com.power.model;

public interface ResourceConstants {
    /**
     * 允许访问的资源路径
     */
    String[] RESOURCE_ALLOW_URLS = {
            "/v2/api-docs",
            "/v3/api-docs",
            "/swagger-resources/configuration/ui",
            "/swagger-resources",
            "/swagger-resources/configuration/security",
            "/webjars/**",
            "/swagger-ui/**",
            "/druid/**",
            "/actuator/**",
            "/api/assistant/**",
            "/order/assistant/**",
            "/flight/assistant/**",
            "/manager/assistant/**",
            "/addr/assistant/**",
            "/addr/listCityFeign/**",
    };
}