package com.project.modifying.application;

import com.project.modifying.domain.Member;
import com.project.modifying.domain.MemberRepository;
import com.project.modifying.domain.Status;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public Member createMember(Status status, Long age) {
        return memberRepository.save(new Member(status, age));
    }

    @Transactional
    public void updateStatusForMembersOlderThan(Long age, Status status) {
        memberRepository.updateStatusForMembersOlderThan(age, status);
    }

    @Transactional(readOnly = true)
    public List<Member> findAllMembers() {
        return memberRepository.findAll();
    }
}
