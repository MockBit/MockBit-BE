package com.example.mockbit.common.auth.infrastructure;

import com.example.mockbit.common.exception.AuthenticationException;
import com.example.mockbit.common.exception.MockbitErrorCode;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;

public class AuthenticationExtractor {

    public String extract(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new AuthenticationException(MockbitErrorCode.TOKEN_NOT_FOUND);
        }

        return Arrays.stream(cookies)
                .filter(cookie -> cookieName.equals(cookie.getName()))
                .findFirst()
                .orElseThrow(() -> new AuthenticationException(MockbitErrorCode.TOKEN_NOT_FOUND))
                .getValue();
    }
}
