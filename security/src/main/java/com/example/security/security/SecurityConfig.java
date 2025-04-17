package com.example.security.security;

import com.example.security.app.application.AuthService;
import com.example.security.app.application.TokenProvider;
import com.example.security.security.authentication.JwtAuthenticationFilter;
import com.example.security.security.authentication.TokenResolver;
import com.example.security.security.exception.AccessDeniedHandler;
import com.example.security.security.exception.AuthenticationEntryPoint;
import com.example.security.security.filter.HttpSecurity;
import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.filter.DelegatingFilterProxy;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenProvider tokenProvider;
    private final TokenResolver tokenResolver;
    private final AuthService authService;

    @Bean
    public Filter securityFilterChain(
            HttpSecurity http,
            AuthenticationEntryPoint authenticationEntryPoint,
            AccessDeniedHandler accessDeniedHandler
    ) {
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(
                tokenProvider,
                tokenResolver,
                authService
        );

        return http
                .exceptionHandling(it -> it
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .authorizeHttpRequests(it -> it
                        .requestMatchers("/login", "/register", "/public/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/private/**").authenticated()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter)
                .build();
    }

    // spring web 의 DelegateFilterProxy 를 대신하는 설정
    // Servlet Container 는 Bean Filter 를 알지 못하므로 FilterRegistrationBean 을 사용하여 등록
    // DelegatingFilterProxy 는 Spring Context 이후 요청이 발생할 때 Lazy Loading 하여 Bean 을 가져옴
    @Bean
    public FilterRegistrationBean<Filter> securityFilter(Filter securityFilterChain) {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        DelegatingFilterProxy delegatingFilterProxy = new DelegatingFilterProxy("securityFilterChain");
        registration.setFilter(delegatingFilterProxy);
        registration.setFilter(securityFilterChain);
        registration.setOrder(1);
        registration.addUrlPatterns("/*");
        return registration;
    }
}
