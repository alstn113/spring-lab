package com.project.auth.ui;

import com.project.auth.application.MemberResponse;
import com.project.auth.application.MemberService;
import com.project.auth.infra.security.Accessor;
import com.project.auth.infra.security.BindAuth;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/members/me")
    public ResponseEntity<MemberResponse> getMe(@BindAuth Accessor accessor) {
        MemberResponse member = memberService.getMemberById(accessor.id());

        return ResponseEntity.ok(member);
    }
}
