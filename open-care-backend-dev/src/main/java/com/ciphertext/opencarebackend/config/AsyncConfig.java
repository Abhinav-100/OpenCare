package com.ciphertext.opencarebackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // ðŸ§µ Thread pool size tuning
        int cores = Runtime.getRuntime().availableProcessors();
        executor.setCorePoolSize(cores);              // minimum threads
        executor.setMaxPoolSize(cores * 2);           // max threads
        executor.setQueueCapacity(500);               // task queue
        executor.setKeepAliveSeconds(60);             // idle thread timeout

        // ðŸ‘€ Monitoring + debugging
        executor.setThreadNamePrefix("async-exec-");

        // Ensures graceful shutdown
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);

        executor.initialize();
        return executor;
    }
}