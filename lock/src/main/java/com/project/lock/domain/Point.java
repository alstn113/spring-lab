package com.project.lock.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import java.util.Objects;
import org.hibernate.Hibernate;

@Entity
public class Point {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long amount;

    @Version
    private Long version;

    protected Point() {
    }

    public Point(Long amount) {
        this(null, amount);
    }

    public Point(Long id, Long amount) {
        this.id = id;
        this.amount = amount;
    }

    public Long subtract(Long point) {
        if (amount < point) {
            throw new IllegalArgumentException("포인트가 부족합니다.");
        }
        amount -= point;
        return amount;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        Point point = (Point) o;
        return Objects.equals(id, point.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public Long getId() {
        return id;
    }

    public Long getAmount() {
        return amount;
    }
}
