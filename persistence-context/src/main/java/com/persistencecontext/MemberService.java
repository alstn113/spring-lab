package com.persistencecontext;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public void updateName(Long id, String newFirstName, String newLastName) {
        Member member = memberRepository.findById(id) // db -> 영속성 컨텍스트, 스냅샷(초기 상태) 저장 -> 조회
                .orElseThrow(() -> new IllegalArgumentException("Member not found for id: " + id));

        // 트랜잭션이 진행되는 동안 엔티티의 속성이 변경됨, jpa 현재 상태와 스냅샷을 비교하여 변경 사항을 감지 및 더티 상태로 인식
        member.updateName(newFirstName, newLastName);

        // 1. 메서드에 @Transactional 이 붙어있으면, 트랜잭션이 시작된다.
        // 2. 엔티티를 조회하면, 영속성 컨텍스트에 엔티티가 저장된다.
        // 3. 엔티티의 속성이 변경되면, 영속성 컨텍스트는 변경 사항을 감지한다.
        // 4. 메서드 종료 시점에 JPA 가 자동으로 flush 를 호출
        // 5. 변경 사항을 쓰기 지연 SQL 저장소에 update query 로 만들어준다. (dirty checking, flush)
        // 6. 쓰기 지연 SQL 저장소에 있는 쿼리를 DB 에 보낸다. (flush)
        // 7. flush 가 완료된 후 트랜잭션 commit 이 된다.
        // 8. 트랜잭션이 종료되면, 영속성 컨텍스트는 해당 트랜잭션과 관련된 모든 엔티티에 대한 관리가 종료됩니다.
    }
}
