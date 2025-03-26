package com.example.mockbit.account.presentation;

import com.example.mockbit.account.application.AccountService;
import com.example.mockbit.common.auth.Login;
import com.example.mockbit.common.auth.application.AuthService;
import com.example.mockbit.common.exception.MockBitException;
import com.example.mockbit.common.exception.MockbitErrorCode;
import com.example.mockbit.common.properties.CookieProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accounts")
@EnableConfigurationProperties(CookieProperties.class)
public class AccountController {

    private final AccountService accountService;
    private final AuthService authService;

    @GetMapping("/balance")
    public Map<String, Object> balanceInfo(
            @CookieValue(name = "accessToken", required = false) String token,
            @Login Long userId
    ) {
        Long tokenUserId = authService.findUserIdByJWT(token);

        if (userId == null && tokenUserId == null) {
            throw new MockBitException(MockbitErrorCode.ONLY_FOR_MEMBER);
        }

        Long effectiveUserId = userId != null ? userId : tokenUserId;
        if (effectiveUserId == null) {
            throw new MockBitException(MockbitErrorCode.ONLY_FOR_MEMBER);
        }

        String balance = accountService.getBalanceByUserId(userId).toString();

        return Map.of("balance", balance);
    }
}
