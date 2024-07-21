package com.project.auth.infra.security;

import static java.util.Objects.requireNonNull;

import java.util.Optional;
import com.project.auth.application.AuthService;
import com.project.auth.application.AuthorizationExtractor;
import com.project.auth.application.MemberResponse;
import com.project.auth.application.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class AuthArgumentResolver implements HandlerMethodArgumentResolver {

    private static final Logger log = LoggerFactory.getLogger(AuthArgumentResolver.class);

    private final AuthorizationExtractor<String> authorizationExtractor;
    private final AuthService authService;
    private final MemberService memberService;

    public AuthArgumentResolver(
            AuthorizationExtractor<String> authorizationExtractor,
            AuthService authService,
            MemberService memberService
    ) {
        this.authorizationExtractor = authorizationExtractor;
        this.authService = authService;
        this.memberService = memberService;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasBindAuthAnnotation = parameter.hasParameterAnnotation(BindAuth.class);
        boolean isAccessorClass = Accessor.class.isAssignableFrom(parameter.getParameterType());

        return hasBindAuthAnnotation && isAccessorClass;
    }

    @Override
    public Accessor resolveArgument(
            @NonNull MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            @NonNull NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        BindAuth bindAuth = requireNonNull(parameter.getParameterAnnotation(BindAuth.class));

        Optional<String> token = authorizationExtractor.extract(webRequest);
        if (token.isEmpty()) {
            log.debug("액세스 토큰이 존재하지 않습니다.");

            if (bindAuth.require()) {
                throw new IllegalStateException("권한이 필요합니다.");
            }

            return Accessor.GUEST;
        }

        Long memberId = authService.getMemberIdByToken(token.get());
        MemberResponse memberResponse = memberService.getMemberById(memberId);
        return new Accessor(memberResponse.id());
    }
}
