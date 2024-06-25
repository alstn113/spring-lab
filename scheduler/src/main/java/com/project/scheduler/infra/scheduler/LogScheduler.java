package com.project.scheduler.infra.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class LogScheduler {

    private static final Logger log = LoggerFactory.getLogger(LogScheduler.class);

    @Scheduled(fixedDelay = 1000) // 이전 작업이 끝나야 1초 후 실행 - 3초 간격
    public void fixedDelayLog() throws InterruptedException {
        log.info("[fixedDelay]");
        Thread.sleep(2000);
    }

    @Scheduled(fixedRate = 1000) // 1초마다 실행 - 1초 간격
    @Async
    public void fixedRateLog() throws InterruptedException {
        log.info("[fixedRate]");
        Thread.sleep(2000);
    }

    @Scheduled(cron = "*/1 * * * * *", zone = "Asia/Seoul") // 1초마다 실행 - 1초 간격, Asia/Seoul 시간대
    @Async
    public void cronLog() throws InterruptedException {
        log.info("[cron]");
        Thread.sleep(2000);
    }
}
