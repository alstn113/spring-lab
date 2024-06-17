package com.project.softdelete.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query(value = "SELECT * FROM member WHERE deleted_at IS NOT NULL", nativeQuery = true)
    List<Member> findDeletedMembers();
}
