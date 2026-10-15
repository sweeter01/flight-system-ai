package com.power.config;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 请求白名单配置类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "gateway.white")
@RefreshScope // 刷新配置
public class WhiteUrlsConfig {

    private List<String> allowUrls = new ArrayList<>();

    @PostConstruct
    public void init() {
        System.out.println("===== 白名单配置: " + allowUrls + " =====");
    }
}