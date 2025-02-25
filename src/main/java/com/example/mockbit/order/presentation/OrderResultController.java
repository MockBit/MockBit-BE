package com.example.mockbit.order.presentation;

import com.example.mockbit.common.auth.application.AuthService;
import com.example.mockbit.common.exception.MockBitException;
import com.example.mockbit.common.exception.MockbitErrorCode;
import com.example.mockbit.common.properties.CookieProperties;
import com.example.mockbit.order.application.OrderResultService;
import com.example.mockbit.order.application.request.MarketOrderAppRequest;
import com.example.mockbit.order.application.response.MarketOrderAppResponse;
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

    @PostMapping("/register")
    public ResponseEntity<MarketOrderAppResponse> marketOrder(
            @Valid @RequestBody MarketOrderAppRequest request,
            @CookieValue(name = "accessToken", required = false) String token
    ) {
        Long userId = authService.findUserIdByJWT(token);

        if (userId == null) {
            throw new MockBitException(MockbitErrorCode.ONLY_FOR_MEMBER);
        }

        OrderResult orderResult = orderResultService.executeMarketOrder(
                userId,
                request.orderPrice(),
                request.leverage(),
                request.position(),
                request.sellOrBuy()
        );

        return ResponseEntity.ok(MarketOrderAppResponse.from(orderResult));
    }
}
