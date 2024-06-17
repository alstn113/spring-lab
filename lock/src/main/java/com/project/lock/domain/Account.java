package com.project.lock.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Objects;
import org.hibernate.Hibernate;

@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long balance;

    protected Account() {
    }

    public Account(Long balance) {
        this(null, balance);
    }

    public Account(Long id, Long balance) {
        this.id = id;
        this.balance = balance;
    }

    public Long withdraw(Long amount) {
        if (balance < amount) {
            throw new IllegalArgumentException("잔고가 부족합니다.");
        }
        balance -= amount;
        return balance;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        Account account = (Account) o;
        return Objects.equals(id, account.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public Long getId() {
        return id;
    }

    public Long getBalance() {
        return balance;
    }
}
