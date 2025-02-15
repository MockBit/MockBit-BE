package com.example.mockbit.common.auth.application;

import com.example.mockbit.common.auth.TokenProvider;
import com.example.mockbit.common.exception.AuthenticationException;
import com.example.mockbit.common.exception.MockbitErrorCode;
import com.example.mockbit.order.application.OrderResultService;
import com.example.mockbit.order.application.OrderService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class AuthService {

    private static final String TOKEN_NAME = "accessToken";
    private static final String CLAIM_SUB = "sub";

    private final TokenProvider tokenProvider;
    private final OrderService orderService;
    private final OrderResultService orderResultService;

    public AuthService(TokenProvider tokenProvider, OrderService orderService, OrderResultService orderResultService) {
        this.tokenProvider = tokenProvider;
        this.orderService = orderService;
        this.orderResultService = orderResultService;
    }

    public String createUserToken(Long userId) {
        Map<String, Object> payload = Map.of(CLAIM_SUB, userId);

        return tokenProvider.createToken(payload);
    }

    public Long findUserIdByJWT(String token) {
        validateToken(token);
        Map<String, Object> payload = tokenProvider.getPayload(token);
        return ((Integer) payload.get(CLAIM_SUB)).longValue();
    }

    private void validateToken(String token) {
        if (!tokenProvider.validateToken(token)) {
            throw new AuthenticationException(MockbitErrorCode.TOKEN_NOT_VALID);
        }
    }

    public String getTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new AuthenticationException(MockbitErrorCode.TOKEN_NOT_FOUND);
        }

        for (Cookie cookie : cookies) {
            if (TOKEN_NAME.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        throw new AuthenticationException(MockbitErrorCode.TOKEN_NOT_FOUND);
    }

    public String getTokenName() {
        return TOKEN_NAME;
    }
}
