package com.project.lock.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

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

    public Long getId() {
        return id;
    }

    public Long getBalance() {
        return balance;
    }
}
