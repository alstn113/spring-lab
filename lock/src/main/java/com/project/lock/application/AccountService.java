package com.project.lock.application;

import com.project.lock.domain.Account;
import com.project.lock.domain.AccountRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional(readOnly = true)
    public Long getBalance(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("계좌를 찾을 수 없습니다."));

        return account.getBalance();
    }

    @Transactional
    public Long withdraw(Long accountId, Long amount) {
        Account account = accountRepository.findByIdWithLock(accountId);

        return account.withdraw(amount);
    }

    @Transactional
    public Account createAccount(Long balance) {
        return accountRepository.save(new Account(balance));
    }
}
