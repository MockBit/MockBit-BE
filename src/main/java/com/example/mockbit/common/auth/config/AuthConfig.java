package com.example.mockbit.common.auth.config;

import com.example.mockbit.common.auth.TokenProvider;
import com.example.mockbit.common.auth.application.AuthService;
import com.example.mockbit.common.auth.infrastructure.AuthenticationExtractor;
import com.example.mockbit.common.auth.infrastructure.JwtTokenProvider;
import com.example.mockbit.common.properties.JwtProperties;
import com.example.mockbit.order.application.OrderResultService;
import com.example.mockbit.order.application.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@EnableConfigurationProperties({JwtProperties.class})
@Configuration
public class AuthConfig {

    private final JwtProperties jwtProperties;
    private final OrderService orderService;
    private final OrderResultService orderResultService;

    @Bean
    public AuthService authService() {
        return new AuthService(tokenProvider(), orderService, orderResultService);
    }

    @Bean
    public TokenProvider tokenProvider() {
        return new JwtTokenProvider(jwtProperties);
    }

    @Bean
    public AuthenticationExtractor authenticationExtractor() {
        return new AuthenticationExtractor();
    }
}
