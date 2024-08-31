package com.persistencecontext;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
class DirtyCheckingTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("더티 체킹 테스트")
    void dirtyChecking() {
        // given
        Member member = new Member("first name", "last name");
        Member savedMember = memberRepository.save(member);

        // when
        // updateName 메서드 내부에서 엔티티의 속성이 변경되면, 영속성 컨텍스트는 변경 사항을 감지한다.
        // 트랜잭션 커밋 시점에 flush -> 더티 체킹 -> DB 반영 -> commit -> 영속성 컨텍스트 초기화
        memberService.updateName(savedMember.getId(), "new first name", "new last name");

        // then
        Member findMember = memberRepository.findById(savedMember.getId())
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        assertThat(findMember.getFirstName()).isEqualTo("new first name");
        assertThat(findMember.getLastName()).isEqualTo("new last name");
    }
}
