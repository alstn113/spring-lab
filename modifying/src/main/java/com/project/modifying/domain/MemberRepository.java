package com.project.modifying.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // clearAutomatically = true: 영속성 컨텍스트를 자동으로 초기화
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Member m SET m.status = :status WHERE m.age >= :age")
    void updateStatusForMembersOlderThan(Long age, Status status);
}
