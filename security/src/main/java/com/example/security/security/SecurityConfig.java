package com.example.security.security;

import com.example.security.app.application.AuthService;
import com.example.security.app.application.TokenProvider;
import com.example.security.security.authentication.JwtAuthenticationFilter;
import com.example.security.security.authentication.TokenResolver;
import com.example.security.security.exception.AccessDeniedHandler;
import com.example.security.security.exception.AuthenticationEntryPoint;
import com.example.security.security.dsl.HttpSecurity;
import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.DelegatingFilterProxyRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String SECURITY_FILTER_CHAIN_BEAN_NAME = "securityFilterChain";

    private final TokenProvider tokenProvider;
    private final TokenResolver tokenResolver;
    private final AuthService authService;

    @Bean(name = SECURITY_FILTER_CHAIN_BEAN_NAME)
    public Filter securityFilterChain(
            HttpSecurity http,
            AuthenticationEntryPoint authenticationEntryPoint,
            AccessDeniedHandler accessDeniedHandler
    ) {
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(
                tokenProvider,
                tokenResolver,
                authService,
                authenticationEntryPoint
        );

        return http
                .addFilterBefore(jwtAuthenticationFilter)
                .exceptionHandling(it -> it
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .authorizeHttpRequests(it -> it
                        .requestMatchers("/login", "/register").permitAll()
                        .requestMatchers(HttpMethod.GET, "/public/**").permitAll()
                        .requestMatchers("/private/member/**").hasAuthority("MEMBER")
                        .requestMatchers("/private/admin/**").hasAuthority("ADMIN")
                        .anyRequest().authenticated())
                .build();
    }

    @Bean
    public DelegatingFilterProxyRegistrationBean securityFilterChainRegistration() {
        DelegatingFilterProxyRegistrationBean registration =
                new DelegatingFilterProxyRegistrationBean(SECURITY_FILTER_CHAIN_BEAN_NAME);
        registration.setOrder(1);
        registration.addUrlPatterns("/*");
        return registration;
    }
}
