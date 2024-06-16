package com.project.modifying;

import static org.assertj.core.api.Assertions.assertThat;

import com.project.modifying.application.MemberService;
import com.project.modifying.domain.Member;
import com.project.modifying.domain.Status;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ModifyingTest {

    @Autowired
    private MemberService memberService;

    @Test
    void bulkUpdate() {
        // given
        memberService.createMember(Status.ACTIVE, 11L);
        memberService.createMember(Status.INACTIVE, 12L);
        memberService.createMember(Status.ACTIVE, 13L);
        memberService.createMember(Status.ACTIVE, 14L);
        memberService.createMember(Status.INACTIVE, 15L);
        memberService.createMember(Status.ACTIVE, 16L);

        // when
        memberService.updateStatusForMembersOlderThan(14L, Status.INACTIVE); // 14세 이상인 회원의 상태를 비활성화로 변경

        // then
        List<Status> statuses = memberService.findAllMembers().stream()
                .map(Member::getStatus)
                .toList();

        assertThat(statuses).containsExactly(
                Status.ACTIVE,
                Status.INACTIVE,
                Status.ACTIVE,
                Status.INACTIVE,
                Status.INACTIVE,
                Status.INACTIVE
        );
    }
}
