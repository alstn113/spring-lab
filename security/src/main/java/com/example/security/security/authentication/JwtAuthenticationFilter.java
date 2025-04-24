package com.example.security.security.authentication;

import com.example.security.app.application.AuthService;
import com.example.security.app.application.TokenProvider;
import com.example.security.app.application.response.MemberInfoResponse;
import com.example.security.app.domain.Role;
import com.example.security.security.authorization.GrantedAuthority;
import com.example.security.security.authorization.SimpleGrantedAuthority;
import com.example.security.security.context.Authentication;
import com.example.security.security.context.SecurityContextHolder;
import com.example.security.security.exception.AuthenticationEntryPoint;
import com.example.security.security.exception.AuthenticationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;
    private final TokenResolver tokenResolver;
    private final AuthService authService;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            Authentication authentication = attemptAuthentication(request);
            if (authentication == null) {
                filterChain.doFilter(request, response);
                return;
            }
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (AuthenticationException e) {
            SecurityContextHolder.clearContext();
            authenticationEntryPoint.commence(request, response, new AuthenticationException("유효하지 않은 토큰입니다."));
        }
    }

    private Authentication attemptAuthentication(HttpServletRequest request) {
        Optional<String> tokenOpt = tokenResolver.extractAccessToken(request);
        if (tokenOpt.isEmpty()) {
            return null;
        }

        Long memberId = tokenProvider.getMemberId(tokenOpt.get());
        MemberInfoResponse memberInfo = authService.getMemberInfo(memberId);

        return new JwtAuthentication(memberInfo.memberId(), mapToAuthorities(memberInfo.role()));
    }

    private Collection<GrantedAuthority> mapToAuthorities(Role... roles) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.toString()));
        }
        return authorities;
    }
}
