package com.power.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;

@Configuration
public class RedisCacheConfig {

    @Bean
    public RedisCacheConfiguration redisCacheConfiguration() {
        // 创建redis缓存配置类
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();
        // 进行redis配置
        redisCacheConfiguration = redisCacheConfiguration
                .serializeValuesWith(RedisSerializationContext
                        .SerializationPair
                        .fromSerializer(RedisSerializer.json()))    // 设置value序列化格式
                .entryTtl(Duration.ofDays(7))   // 设置过期时间
                .disableCachingNullValues();  // 禁用value使用空值
        return redisCacheConfiguration;
    }
}
