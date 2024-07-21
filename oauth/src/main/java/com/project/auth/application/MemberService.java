package com.project.auth.application;

import com.project.auth.domain.Member;
import com.project.auth.domain.MemberRepository;
import com.project.auth.domain.Provider;
import com.project.auth.infra.oauth.SocialProfile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public MemberResponse getMemberById(Long id) {
        return memberRepository.findById(id)
                .map(MemberResponse::from)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
    }

    public MemberResponse findOrCreateMember(SocialProfile profile, Provider provider) {
        Member member = memberRepository.findBySocialIdAndProvider(profile.id(), provider)
                .orElseGet(() -> memberRepository.save(profile.toMember(provider)));

        return MemberResponse.from(member);
    }
}
