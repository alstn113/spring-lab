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

    @Bean
    public FilterRegistrationBean<Filter> securityFilter(Filter securityFilterChain) {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        registration.setFilter(securityFilterChain);
        registration.setOrder(1);
        registration.addUrlPatterns("/*");
        return registration;
    }
}
