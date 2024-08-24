package transaction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import transaction.application.AccountService;
import transaction.domain.Account;
import transaction.domain.AccountRepository;
import transaction.domain.LogRepository;

@SpringBootTest
class PropagationTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private LogRepository logRepository;

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();
        logRepository.deleteAll();
    }

    @Test
    void 성공_테스트() {
        // given
        Account account = createAccount();
        boolean outerFail = false;
        boolean innerFail = false;

        // when
        accountService.withdraw(account.getId(), 1000L, outerFail, innerFail);

        // then
        Account foundAccount = accountRepository.getAccountById(account.getId());
        assertThat(foundAccount.getBalance()).isEqualTo(9000L);
    }

    /**
     * REQUIRES_NEW 트랜잭션을 사용할 경우
     * 내부 트랜잭션을 커밋하고 상위 트랜잭션에서 실패해도 내부 트랜잭션은 롤백되지 않는다.
     */
    @Test
    void 외부_트랜잭션_실패_테스트() {
        // given
        Account account = createAccount();
        long id = account.getId();
        boolean outerFail = true;
        boolean innerFail = false;

        // when & then
        assertAll(
                () -> assertThatThrownBy(() -> accountService.withdraw(id, 1000L, outerFail, innerFail))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessage("출금 실패"),
                () -> assertThat(logRepository.findAll()).hasSize(1)
        );
    }

    /**
     * REQUIRES_NEW 트랜잭션을 사용할 경우
     * 내부 트랜잭션 실패 시 롤백하고 상위 트랜잭션은 실패 시 롤백하고 성공 시 커밋된다.
     */
    @Test
    void 내부_트랜잭션_실패_테스트() {
        // given
        Account account = createAccount();
        long id = account.getId();
        boolean outerFail = false;
        boolean innerFail = true;

        // when & then
        assertAll(
                () -> assertThatThrownBy(() -> accountService.withdraw(id, 1000L, outerFail, innerFail))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessage("로그 실패"),
                () -> assertThat(logRepository.findAll()).isEmpty(),
                () -> assertThat(accountRepository.getAccountById(id).getBalance()).isEqualTo(10000L)
        );
    }

    private Account createAccount() {
        return accountRepository.save(new Account("test", 10000L));
    }
}
