package com.project.lock.domain;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface PointRepository extends JpaRepository<Point, Long> {

    // 낙관적 락
    @Lock(LockModeType.OPTIMISTIC)
    @Query("select p from Point p where p.id = :id")
    Point findByIdWithLock(Long id);
}
