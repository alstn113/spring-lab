package com.project.modifying.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(nullable = false)
    private Long age;

    protected Member() {
    }

    public Member(Status status, Long age) {
        this(null, status, age);
    }

    public Member(Long id, Status status, Long age) {
        this.id = id;
        this.status = status;
        this.age = age;
    }

    public Long getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public Long getAge() {
        return age;
    }
}
