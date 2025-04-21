package com.example.mockbit.common.config;

import com.example.mockbit.common.auth.application.AuthService;
import com.example.mockbit.common.auth.infrastructure.AdminInterceptor;
import com.example.mockbit.common.auth.infrastructure.AuthenticationExtractor;
import com.example.mockbit.common.auth.infrastructure.AuthenticationPrincipalArgumentResolver;
import com.example.mockbit.common.auth.infrastructure.UserInterceptor;
import com.example.mockbit.common.properties.CorsProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@RequiredArgsConstructor
@EnableConfigurationProperties({CorsProperties.class})
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthService authService;
    private final AuthenticationExtractor authenticationExtractor;
    private final CorsProperties corsProperties;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(corsProperties.maxAge());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminInterceptor())
                .addPathPatterns("/api/admin/**");

        registry.addInterceptor(userInterceptor())
                .addPathPatterns("/api/limit/orders/**", "/api/market/orders/**");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authenticationPrincipalArgumentResolver());
    }

    @Bean
    public AdminInterceptor adminInterceptor() {
        return new AdminInterceptor(authService, authenticationExtractor);
    }

    @Bean
    public UserInterceptor userInterceptor() {
        return new UserInterceptor(authService, authenticationExtractor);
    }

    @Bean
    public AuthenticationPrincipalArgumentResolver authenticationPrincipalArgumentResolver() {
        return new AuthenticationPrincipalArgumentResolver(authService, authenticationExtractor);
    }
}
