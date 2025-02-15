package com.example.mockbit.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("cors")
public record CorsProperties(
        Long maxAge,
        String[] allowedOrigins
) {
}
