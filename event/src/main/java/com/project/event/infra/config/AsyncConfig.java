package com.project.event.infra.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncConfig {

    @Bean
    public TaskExecutor asyncExecutor() {
        ThreadPoolTaskExecutor asyncExecutor = new ThreadPoolTaskExecutor();
        asyncExecutor.setThreadNamePrefix("async-pool");
        asyncExecutor.setCorePoolSize(10);
        asyncExecutor.initialize();
        return asyncExecutor;
    }
}
