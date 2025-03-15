package com.example.mockbit.common.auth.infrastructure;

import com.example.mockbit.common.auth.Login;
import com.example.mockbit.common.auth.application.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@RequiredArgsConstructor
public class AuthenticationPrincipalArgumentResolver implements HandlerMethodArgumentResolver {

    private final AuthService authService;
    private final AuthenticationExtractor authenticationExtractor;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Login.class) &&
                parameter.getParameterType().equals(Long.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        String jwt = authenticationExtractor.extract(request, authService.getTokenName());
        return authService.findUserIdByJWT(jwt);
    }
}
