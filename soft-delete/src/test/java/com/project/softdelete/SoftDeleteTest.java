package com.project.softdelete;

import static org.assertj.core.api.Assertions.assertThat;

import com.project.softdelete.application.MemberService;
import com.project.softdelete.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SoftDeleteTest {

    @Autowired
    private MemberService memberService;

    @Test
    @DisplayName("""
            삭제된 회원을 조회하는 테스트
            - 회원을 삭제한 후, 삭제된 회원을 조회하는지 확인
            """)
    void findDeletedMembers() {
        // given
        Member memberA = memberService.createMember("memberA");
        Member memberB = memberService.createMember("memberB");

        // when
        memberService.deleteById(memberA.getId());

        // then
        assertThat(memberService.getMembers()).containsExactly(memberB);
        assertThat(memberService.getDeletedMembers()).containsExactly(memberA) // 삭제된 회원을 조회
                .extracting(Member::getDeletedAt).isNotNull(); // 삭제된 회원의 삭제된 시간 존재
    }
}
