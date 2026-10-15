package com.power.config;


import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.HashSet;

// ... existing code ...

@Configuration
@EnableConfigurationProperties(SwaggerProperties.class)
public class SwaggerAutoConfiguration {

    @Autowired
    private SwaggerProperties swaggerProperties;

    /**
     * 项目环境对象
     */
    @Autowired
    private Environment environment;

    /**
     * 设置swagger文档对象
     * 1.swagger文档只有在开发阶段生成，生产环境不需要生成文档
     * @return
     */
    @Bean
    public Docket docket() {
        // 生成文档的开关，true默认开启状态
        Boolean flag = true;
        // 从项目环境对象中获取项目运行环境标识
        String[] activeProfiles = environment.getActiveProfiles();
        // 循环遍历
        for (String activeProfile : activeProfiles) {
            if (activeProfile.equals("pro")) {
                // pro一般表示生产环境
                flag = false;
                break;
            }
        }
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(getApiInfo())  // 文档描述信息对象
                .enable(flag)   // 是否生成文档的开关
                .select()
                .apis(RequestHandlerSelectors.basePackage(swaggerProperties.getBasePackage())) // 基于包名描述swagger注解
                .build();
    }

    private ApiInfo getApiInfo() {
        Contact contact = new Contact(
                swaggerProperties.getName(),
                swaggerProperties.getUrl(),
                swaggerProperties.getEmail()
        );
        ApiInfo apiInfo = new ApiInfo(
                swaggerProperties.getTitle(),
                swaggerProperties.getDescription(),
                swaggerProperties.getVersion(),
                swaggerProperties.getTermsOfServiceUrl(),
                contact,
                swaggerProperties.getLicense(),
                swaggerProperties.getLicenseUrl(),
                new HashSet<>()
        );
        return apiInfo;
    }

}
