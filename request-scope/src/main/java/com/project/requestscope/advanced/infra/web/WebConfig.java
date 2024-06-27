package com.project.requestscope.advanced.infra.web;

import com.project.requestscope.advanced.infra.security.AuthArgumentResolver;
import com.project.requestscope.advanced.infra.security.AuthContext;
import com.project.requestscope.advanced.infra.security.AuthInterceptor;
import com.project.requestscope.advanced.infra.security.AuthorizationExtractor;
import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AuthContext authContext;
    private final AuthorizationExtractor<String> authorizationExtractor;

    public WebConfig(AuthContext authContext, AuthorizationExtractor<String> authorizationExtractor) {
        this.authContext = authContext;
        this.authorizationExtractor = authorizationExtractor;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new AuthArgumentResolver(authorizationExtractor, authContext));
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor(authorizationExtractor, authContext))
                .addPathPatterns("/check/**");
    }
}
