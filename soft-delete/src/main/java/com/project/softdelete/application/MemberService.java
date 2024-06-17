package com.project.softdelete.application;

import com.project.softdelete.domain.Member;
import com.project.softdelete.domain.MemberRepository;
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
    public Member createMember(String name) {
        return memberRepository.save(new Member(name));
    }

    @Transactional(readOnly = true)
    public List<Member> getDeletedMembers() {
        return memberRepository.findDeletedMembers();
    }

    @Transactional(readOnly = true)
    public List<Member> getMembers() {
        return memberRepository.findAll();
    }

    @Transactional
    public void deleteById(Long id) {
        memberRepository.deleteById(id);
    }
}
