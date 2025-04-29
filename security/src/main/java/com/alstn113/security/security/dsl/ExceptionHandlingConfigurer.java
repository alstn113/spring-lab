package com.alstn113.security.security.dsl;

import com.alstn113.security.security.exception.AccessDeniedHandler;
import com.alstn113.security.security.exception.AuthenticationEntryPoint;
import com.alstn113.security.security.filter.ExceptionTranslationFilter;

public class ExceptionHandlingConfigurer {

    private AuthenticationEntryPoint authenticationEntryPoint;
    private AccessDeniedHandler accessDeniedHandler;

    public ExceptionHandlingConfigurer(
            AuthenticationEntryPoint authenticationEntryPoint,
            AccessDeniedHandler accessDeniedHandler
    ) {
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    public void configure(HttpSecurity httpSecurity) {
        ExceptionTranslationFilter exceptionTranslationFilter = new ExceptionTranslationFilter(
                authenticationEntryPoint,
                accessDeniedHandler
        );
        httpSecurity.addFilter(exceptionTranslationFilter);
    }

    public ExceptionHandlingConfigurer authenticationEntryPoint(
            AuthenticationEntryPoint authenticationEntryPoint
    ) {
        this.authenticationEntryPoint = authenticationEntryPoint;
        return this;
    }

    public ExceptionHandlingConfigurer accessDeniedHandler(
            AccessDeniedHandler accessDeniedHandler
    ) {
        this.accessDeniedHandler = accessDeniedHandler;
        return this;
    }
}
