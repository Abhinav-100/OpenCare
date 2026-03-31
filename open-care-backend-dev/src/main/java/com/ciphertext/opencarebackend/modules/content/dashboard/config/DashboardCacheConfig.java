package com.ciphertext.opencarebackend.modules.content.dashboard.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class DashboardCacheConfig {

    @Bean
    public CacheManagerCustomizer<CaffeineCacheManager> dashboardCacheCustomizer() {
        return cacheManager -> {
            // Register specific caches with their own configurations
            cacheManager.registerCustomCache("dashboardOverview",
                    Caffeine.newBuilder()
                            .maximumSize(10)
                            .expireAfterWrite(5, TimeUnit.MINUTES)
                            .recordStats()
                            .build());

            cacheManager.registerCustomCache("bloodBankData",
                    Caffeine.newBuilder()
                            .maximumSize(10)
                            .expireAfterWrite(2, TimeUnit.MINUTES)
                            .recordStats()
                            .build());

            cacheManager.registerCustomCache("registrationTrends",
                    Caffeine.newBuilder()
                            .maximumSize(50)
                            .expireAfterWrite(1, TimeUnit.HOURS)
                            .recordStats()
                            .build());

            cacheManager.registerCustomCache("systemAlerts",
                    Caffeine.newBuilder()
                            .maximumSize(100)
                            .expireAfterWrite(1, TimeUnit.MINUTES)
                            .recordStats()
                            .build());

            cacheManager.registerCustomCache("recentActivities",
                    Caffeine.newBuilder()
                            .maximumSize(200)
                            .expireAfterWrite(3, TimeUnit.MINUTES)
                            .recordStats()
                            .build());
        };
    }
}