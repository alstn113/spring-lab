package transaction.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import transaction.domain.Account;
import transaction.domain.AccountRepository;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final LogService logService;

    public AccountService(AccountRepository accountRepository, LogService logService) {
        this.accountRepository = accountRepository;
        this.logService = logService;
    }

    @Transactional
    public void withdraw(long id, long amount, boolean outerFail, boolean innerFail) {
        Account account = accountRepository.getAccountById(id);
        account.withdraw(amount);

        logService.logWithRequiresNew("withdraw", innerFail);

        if (outerFail) {
            throw new IllegalArgumentException("출금 실패");
        }
    }
}
