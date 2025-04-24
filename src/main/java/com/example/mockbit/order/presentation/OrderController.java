package com.example.mockbit.order.presentation;

import com.example.mockbit.common.auth.Login;
import com.example.mockbit.common.exception.MockBitException;
import com.example.mockbit.common.exception.MockbitErrorCode;
import com.example.mockbit.common.properties.CookieProperties;
import com.example.mockbit.order.application.OrderService;
import com.example.mockbit.order.application.request.BuyLimitOrderAppRequest;
import com.example.mockbit.order.application.request.SellLimitOrderAppRequest;
import com.example.mockbit.order.application.request.UpdateOrderAppRequest;
import com.example.mockbit.order.application.response.BuyLimitOrderAppResponse;
import com.example.mockbit.order.application.response.SellLimitOrderAppResponse;
import com.example.mockbit.order.application.response.UpdateOrderAppResponse;
import com.example.mockbit.order.domain.Order;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
    public ResponseEntity<List<BuyLimitOrderAppResponse>> getUserOrders(
            @Login Long userId
    ) {
        List<Order> orders = orderService.findOrderByUserId(userId);
        if (orders.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(
                    orders.stream().map(BuyLimitOrderAppResponse::of).collect(Collectors.toList())
            );
        }
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

    @PutMapping("/update/orders/{orderId}")
    public ResponseEntity<UpdateOrderAppResponse> updateOrder(
            @PathVariable String orderId,
            @Valid @RequestBody UpdateOrderAppRequest request,
            @Login Long userId
    ) {
        Order updatedOrder = orderService.updateOrder(orderId, request, userId);
        return ResponseEntity.ok(UpdateOrderAppResponse.from(updatedOrder));
    }
}
