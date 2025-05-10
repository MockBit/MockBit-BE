package com.example.mockbit.user.presentation;

import com.example.mockbit.common.auth.Login;
import com.example.mockbit.common.auth.application.AuthService;
import com.example.mockbit.common.exception.AuthenticationException;
import com.example.mockbit.common.properties.CookieProperties;
import com.example.mockbit.user.application.request.UserLoginAppRequest;
import com.example.mockbit.user.application.response.UserAppResponse;
import com.example.mockbit.user.application.UserService;
import com.example.mockbit.user.application.request.UserJoinAppRequest;
import com.example.mockbit.user.application.request.UserUpdateAppRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@EnableConfigurationProperties({CookieProperties.class})
public class UserController {

    private final UserService userService;
    private final AuthService authService;
    private final CookieProperties cookieProperties;

    @PostMapping
    public ResponseEntity<Long> createUser(
            @Valid @RequestBody UserJoinAppRequest request
    ) {
        Long id = userService.join(request);
        return ResponseEntity.ok(id);
    }

    @GetMapping("/check-id")
    public ResponseEntity<Map<String, Boolean>> checkUserId(@RequestParam String userid) {
        Map<String, Boolean> response = new HashMap<>();
        response.put("available", userService.isUseridAvailable(userid));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check-nickname")
    public ResponseEntity<Map<String, Boolean>> checkNickname(@RequestParam String nickname) {
        Map<String, Boolean> response = new HashMap<>();
        response.put("available", userService.isNicknameAvailable(nickname));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@Valid @RequestBody UserLoginAppRequest request) {
        String token = userService.login(request);
        ResponseCookie responseCookie = createResponseCookie(token);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        ResponseCookie expiredCookie = expireCookie();
        response.setHeader(HttpHeaders.SET_COOKIE, expiredCookie.toString());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserAppResponse> getUser(@PathVariable Long id) {
        UserAppResponse user = userService.findById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> checkLoginStatus(
            @CookieValue(name = "accessToken", required = false) String token,
            @Login Long userId
    ) {
        Map<String, Object> response = new HashMap<>();
        response.put("isLoggedIn", userService.isLoggedIn(token, userId));
        if (userId != null) {
            response.put("userId", userId);
        }
        return ResponseEntity.ok(response);
    }

    @PatchMapping
    public ResponseEntity<Void> updateUser(
            @Login Long id,
            @Valid @RequestBody UserUpdateAppRequest request
    ) {
        userService.updateUser(id, request);
        return ResponseEntity.noContent().build();
    }

    private ResponseCookie expireCookie() {
        return ResponseCookie.from(authService.getTokenName(), null)
                .httpOnly(cookieProperties.httpOnly())
                .secure(cookieProperties.secure())
                .domain(cookieProperties.domain())
                .path(cookieProperties.path())
                .sameSite(cookieProperties.sameSite())
                .maxAge(0L)
                .build();
    }

    private ResponseCookie createResponseCookie(String token) {
        return ResponseCookie.from(authService.getTokenName(), token)
                .httpOnly(cookieProperties.httpOnly())
                .secure(cookieProperties.secure())
                .domain(cookieProperties.domain())
                .path(cookieProperties.path())
                .sameSite(cookieProperties.sameSite())
                .maxAge(cookieProperties.maxAge())
                .build();
    }
}
