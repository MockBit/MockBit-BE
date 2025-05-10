package com.example.mockbit.order.presentation;

import com.example.mockbit.common.auth.Login;
import com.example.mockbit.common.properties.CookieProperties;
import com.example.mockbit.order.application.OrderService;
import com.example.mockbit.order.application.request.BuyLimitOrderAppRequest;
import com.example.mockbit.order.application.request.CancelLimitOrderAppRequest;
import com.example.mockbit.order.application.request.SellLimitOrderAppRequest;
import com.example.mockbit.order.application.response.BuyLimitOrderAppResponse;
import com.example.mockbit.order.application.response.CancelLimitOrderAppResponse;
import com.example.mockbit.order.application.response.PendingLimitOrdersAppResponse;
import com.example.mockbit.order.application.response.SellLimitOrderAppResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

    @DeleteMapping("/cancel/orders")
    public ResponseEntity<CancelLimitOrderAppResponse> cancelOrder(
            @Valid @RequestBody CancelLimitOrderAppRequest request,
            @Login Long userId
    ) {
        CancelLimitOrderAppResponse response = orderService.deleteOrderByOrderId(userId, request);
        return ResponseEntity.ok(response);
    }
}
