package com.example.mockbit.account.presentation;

import com.example.mockbit.account.application.AccountService;
import com.example.mockbit.account.application.BtcService;
import com.example.mockbit.common.auth.Login;
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
    private final BtcService btcService;

    @GetMapping("/balance")
    public Map<String, Object> balanceInfo(
            @Login Long userId
    ) {
        String balance = accountService.getBalanceByUserId(userId).toString();
        return Map.of("balance", balance);
    }

    @GetMapping("/btc")
    public Map<String, Object> btcInfo(
            @Login Long userId
    ) {
        String btc = btcService.getBtcBalanceByUserId(userId);
        return Map.of("btc", btc);
    }

    @GetMapping("/avg-entry-price")
    public Map<String, Object> avgEntryPriceInfo(
            @Login Long userId
    ) {
        String avgEntryPrice = btcService.getAvgEntryPriceByUserId(userId);
        return Map.of("avgEntryPrice", avgEntryPrice);
    }

    @GetMapping("/liquidation-price")
    public Map<String, Object> liquidationPriceInfo(
            @Login Long userId
    ) {
        String liquidationPrice = btcService.getLiquidationPriceByUserId(userId);
        return Map.of("liquidationPrice", liquidationPrice);
    }
}
