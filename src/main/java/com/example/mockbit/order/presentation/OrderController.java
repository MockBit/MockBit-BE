package com.example.mockbit.order.presentation;

import com.example.mockbit.common.auth.Login;
import com.example.mockbit.common.auth.application.AuthService;
import com.example.mockbit.common.exception.MockBitException;
import com.example.mockbit.common.exception.MockbitErrorCode;
import com.example.mockbit.common.properties.CookieProperties;
import com.example.mockbit.order.application.OrderService;
import com.example.mockbit.order.application.request.OrderAppRequest;
import com.example.mockbit.order.application.request.UpdateOrderAppRequest;
import com.example.mockbit.order.application.response.OrderAppResponse;
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

    @PostMapping("/register")
    public ResponseEntity<OrderAppResponse> order(
            @Valid @RequestBody OrderAppRequest request,
            @Login Long userId
    ) {

        Order order = orderService.saveOrder(userId, request);

        return ResponseEntity.ok(OrderAppResponse.from(order));
    }

    @GetMapping("/pending/orders")
    public ResponseEntity<List<OrderAppResponse>> getUserOrders(
            @Login Long userId
    ) {

        List<Order> orders = orderService.findOrderByUserId(userId);

        if (orders.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(
                orders.stream().map(OrderAppResponse::from).collect(Collectors.toList())
        );
    }

    @DeleteMapping("/cancel/orders/{orderId}")
    public ResponseEntity<OrderAppResponse> cancelOrder(
            @PathVariable String orderId,
            @Login Long userId
    ) {

        Order order = orderService.findOrderById(orderId)
                .orElseThrow(() -> new MockBitException(MockbitErrorCode.NO_ORDER_RESOURCE));

        if (!Objects.equals(order.getUserId(), userId)) {
            throw new MockBitException(MockbitErrorCode.USER_ID_NOT_EQUALS_ORDER);
        }
        orderService.deleteOrderById(orderId);

        return ResponseEntity.ok(OrderAppResponse.from(order));
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
