package com.project.lock.application;

import com.project.lock.domain.Point;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PessimisticLockTest {

    @Autowired
    private PointService pointService;

    @Test
    @DisplayName("""
            여러 스레드에서 동시에 포인트를 차감할 때, 포인트 차감이 정상적으로 처리되는지 확인하는 테스트 (낙관적 락)
            - 포인트가 10,000점인 계좌를 생성한 후, 5개의 스레드에서 각각 3,000점을 차감하도록 시도
            - 첫번째만 차감에 성공하고, 나머지는 버전이 다르다는 예외가 발생하는 것을 확인
            """)
    void subtractPoint() throws InterruptedException {
        // given
        Point point = pointService.createPoint(10_000L);

        int numberOfExecute = 5;

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        ExecutorService executorsService = Executors.newFixedThreadPool(numberOfExecute);
        CountDownLatch latch = new CountDownLatch(numberOfExecute);

        // when
        for (int i = 0; i < numberOfExecute; i++) {
            executorsService.submit(() -> {
                try {
                    pointService.subtractPoint(point.getId(), 3_000L);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        // then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(pointService.getPoint(point.getId())).isEqualTo(7_000L);
            softly.assertThat(successCount.get()).isEqualTo(1);
            softly.assertThat(failCount.get()).isEqualTo(4);
        });
    }
}
