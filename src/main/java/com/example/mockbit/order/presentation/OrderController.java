package com.example.mockbit.order.presentation;

import com.example.mockbit.common.auth.Login;
import com.example.mockbit.common.exception.MockBitException;
import com.example.mockbit.common.exception.MockbitErrorCode;
import com.example.mockbit.common.properties.CookieProperties;
import com.example.mockbit.order.application.OrderService;
import com.example.mockbit.order.application.request.BuyLimitOrderAppRequest;
import com.example.mockbit.order.application.request.SellLimitOrderAppRequest;
import com.example.mockbit.order.application.response.BuyLimitOrderAppResponse;
import com.example.mockbit.order.application.response.PendingLimitOrdersAppResponse;
import com.example.mockbit.order.application.response.SellLimitOrderAppResponse;
import com.example.mockbit.order.domain.Order;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/limit/orders")
@EnableConfigurationProperties(CookieProperties.class)
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/buy")
    public ResponseEntity<BuyLimitOrderAppResponse> buyLimitOrder(
            @Valid @RequestBody BuyLimitOrderAppRequest request,
            @Login Long userId
    ) {
        BuyLimitOrderAppResponse response = orderService.executeBuyLimitOrder(userId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/sell")
    public ResponseEntity<SellLimitOrderAppResponse> sellLimitOrder(
            @Valid @RequestBody SellLimitOrderAppRequest request,
            @Login Long userId
    ) {
        SellLimitOrderAppResponse response = orderService.executeSellLimitOrder(userId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pending/orders")
    public ResponseEntity<PendingLimitOrdersAppResponse> getPendingOrders(
            @Login Long userId
    ) {
        PendingLimitOrdersAppResponse response = orderService.findOrderByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/cancel/orders/{orderId}")
    public ResponseEntity<BuyLimitOrderAppResponse> cancelOrder(
            @PathVariable String orderId,
            @Login Long userId
    ) {
        Order order = orderService.findOrderById(orderId)
                .orElseThrow(() -> new MockBitException(MockbitErrorCode.NO_ORDER_RESOURCE));

        if (!Objects.equals(order.getUserId(), userId)) {
            throw new MockBitException(MockbitErrorCode.USER_ID_NOT_EQUALS_ORDER);
        }
        orderService.deleteOrderById(orderId);

        return ResponseEntity.ok(BuyLimitOrderAppResponse.of(order));
    }
}
