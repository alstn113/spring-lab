package com.project.lock.application;

import com.project.lock.domain.Account;
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
class OptimisticLockTest {

    @Autowired
    private AccountService accountService;

    @Test
    @DisplayName("""
            여러 스레드에서 동시에 출금을 시도할 때, 출금이 정상적으로 처리되는지 확인하는 테스트 (비관적 락)
            - 잔액이 5,000원인 계좌를 생성한 후, 5개의 스레드에서 각각 3,000원을 출금하도록 시도
            - 3개의 스레드에서는 출금에 성공하고, 2개의 스레드에서는 출금에 실패하는 것을 확인
            """)
    void withdraw() throws InterruptedException {
        // given
        Account account = accountService.createAccount(10_000L);

        int numberOfExecute = 5;

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        ExecutorService executorsService = Executors.newFixedThreadPool(numberOfExecute);
        CountDownLatch latch = new CountDownLatch(numberOfExecute);

        // when
        for (int i = 0; i < numberOfExecute; i++) {
            executorsService.submit(() -> {
                try {
                    accountService.withdraw(account.getId(), 3_000L);
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
            softly.assertThat(accountService.getBalance(account.getId())).isEqualTo(1_000L);
            softly.assertThat(successCount.get()).isEqualTo(3);
            softly.assertThat(failCount.get()).isEqualTo(2);
        });
    }
}
