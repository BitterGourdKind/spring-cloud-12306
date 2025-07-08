package com.jiawa.train.business.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class CaffeineConfig {
    /**
     * 本地缓存caffine 配置
     */
    @Bean
    public Cache<String, Object> caffeineCache() {
        return Caffeine.newBuilder()
                .initialCapacity(1024)  //初始大小
                .maximumSize(10_000L) // 最大10000条
                .expireAfterWrite(Duration.ofMinutes(5)) // 5分钟过期
                .build();
    }
}
