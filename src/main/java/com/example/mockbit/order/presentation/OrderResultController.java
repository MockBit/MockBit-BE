package com.example.mockbit.order.presentation;

import com.example.mockbit.common.auth.Login;
import com.example.mockbit.common.auth.application.AuthService;
import com.example.mockbit.common.exception.MockBitException;
import com.example.mockbit.common.exception.MockbitErrorCode;
import com.example.mockbit.common.properties.CookieProperties;
import com.example.mockbit.order.application.OrderResultService;
import com.example.mockbit.order.application.request.BuyMarketOrderAppRequest;
import com.example.mockbit.order.application.request.SellMarketOrderAppRequest;
import com.example.mockbit.order.application.response.BuyMarketOrderAppResponse;
import com.example.mockbit.order.application.response.SellMarketOrderAppResponse;
import com.example.mockbit.order.domain.OrderResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/market/orders")
@EnableConfigurationProperties(CookieProperties.class)
public class OrderResultController {

    private final OrderResultService orderResultService;
    private final AuthService authService;

    @PostMapping("/buy")
    public ResponseEntity<BuyMarketOrderAppResponse> buyMarketOrder(
            @Valid @RequestBody BuyMarketOrderAppRequest request,
            @CookieValue(name = "accessToken", required = false) String token,
            @Login Long userId
    ) {
        Long tokenUserId = authService.findUserIdByJWT(token);

        if (userId == null && tokenUserId == null) {
            throw new MockBitException(MockbitErrorCode.ONLY_FOR_MEMBER);
        }

        OrderResult orderResult = orderResultService.executeBuyMarketOrder(
                userId,
                request.orderPrice(),
                request.leverage(),
                request.position(),
                request.sellOrBuy()
        );

        return ResponseEntity.ok(BuyMarketOrderAppResponse.from(orderResult));
    }

    @PostMapping("/sell")
    public ResponseEntity<SellMarketOrderAppResponse> sellMarketOrder(
            @Valid @RequestBody SellMarketOrderAppRequest request,
            @CookieValue(name = "accessToken", required = false) String token,
            @Login Long userId
    ) {
        Long tokenUserId = authService.findUserIdByJWT(token);

        if (userId == null && tokenUserId == null) {
            throw new MockBitException(MockbitErrorCode.ONLY_FOR_MEMBER);
        }

        OrderResult orderResult = orderResultService.executeSellMarketOrder(
                userId,
                request.btcAmount(),
                request.position(),
                request.sellOrBuy()
        );

        return ResponseEntity.ok(SellMarketOrderAppResponse.from(orderResult));
    }
}
