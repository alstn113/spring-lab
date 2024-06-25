package com.project.scheduler;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.project.scheduler.infra.scheduler.LogScheduler;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

@SpringBootTest
class SchedulerTest {

    @SpyBean
    private LogScheduler logScheduler;

    @Test
    @DisplayName("""
            fixedDelayLog [동기]은 이전 작업이 끝나야 실행되므로 3초 간격으로 2번 실행된다.
            fixedRateLog [비동기]은 1초 간격으로 5번 실행된다.
            cronLog [비동기]은 1초 간격으로 5번 실행된다.
            """)
    void fixedDelayLog() {
        await().atMost(Duration.of(5, ChronoUnit.SECONDS)).untilAsserted(() -> {
                    verify(logScheduler, times(2)).fixedDelayLog();
                    verify(logScheduler, times(5)).fixedRateLog();
                    verify(logScheduler, times(5)).cronLog();
                }
        );
    }
}
