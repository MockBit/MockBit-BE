package com.example.mockbit.common.auth.infrastructure;

import com.example.mockbit.common.auth.application.AuthService;
import com.example.mockbit.common.exception.AuthenticationException;
import com.example.mockbit.common.exception.MockbitErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor
public class UserInterceptor implements HandlerInterceptor {

    private final AuthService authService;
    private final AuthenticationExtractor authenticationExtractor;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpMethod method = HttpMethod.valueOf(request.getMethod());
        if (HttpMethod.OPTIONS.equals(method)) {
            return true;
        }

        try {
            String jwt = authenticationExtractor.extract(request, authService.getTokenName());
            Long userId = authService.findUserIdByJWT(jwt);
            if (userId == null) {
                throw new AuthenticationException(MockbitErrorCode.TOKEN_NOT_VALID);
            }
            request.setAttribute("loginUserId", userId);
        } catch (AuthenticationException e) {
            throw new AuthenticationException(MockbitErrorCode.ONLY_FOR_MEMBER);
        }

        return true;
    }
}
