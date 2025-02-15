package com.example.mockbit.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties("cookie")
public record CookieProperties(
        boolean httpOnly,
        boolean secure,
        String domain,
        String path,
        String sameSite,
        Duration maxAge
) {
}
