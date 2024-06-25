package com.project.scheduler.infra.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class SchedulerConfig {

    private final Logger log = LoggerFactory.getLogger(SchedulerConfig.class);

    // public class SchedulerConfig implements SchedulingConfigurer 로 하면 Async에 빈 이름 안 넣어준 것들은
    // task-1, task-2, ... 이런식으로 이름이 붙는다. 뭔지 잘 모르겠다.
    @Bean(destroyMethod = "shutdown") // 스케줄러 종료 메서드
    public ThreadPoolTaskScheduler myTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10); // 스케줄러의 pool size
        scheduler.setThreadNamePrefix("MyScheduler-"); // 로그에 출력되는 스레드 이름
        scheduler.setErrorHandler(t -> log.error("Error occurs", t)); // 에러 핸들러 설정
        scheduler.initialize(); // 초기화 작업 수행 후 스케줄러에 할당
        return scheduler;
    }
}
