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
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/limit/orders")
@EnableConfigurationProperties(CookieProperties.class)
public class OrderController {

    private final OrderService orderService;
    private final HttpSession session;
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<OrderAppResponse> order(
            @Valid @RequestBody OrderAppRequest request,
            @CookieValue(name = "accessToken", required = false) String token
    ) {
        Long userId = authService.findUserIdByJWT(token);

        if (userId == null) {
            throw new MockBitException(MockbitErrorCode.ONLY_FOR_MEMBER);
        }

        Order order = orderService.saveOrder(
                userId,
                request.price(),
                request.btcPrice(),
                request.orderPrice(),
                request.leverage(),
                request.position(),
                request.sellOrBuy()
        );

        return ResponseEntity.ok(OrderAppResponse.from(order));
    }

    @GetMapping("/pending/orders")
    public ResponseEntity<List<OrderAppResponse>> getUserOrders(
            @CookieValue(name = "accessToken", required = false) String token
    ) {
        Long userId = authService.findUserIdByJWT(token);

        if (userId == null) {
            throw new MockBitException(MockbitErrorCode.ONLY_FOR_MEMBER);
        }

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
            @CookieValue(name = "accessToken", required = false) String token
    ) {
        Long userId = authService.findUserIdByJWT(token);

        if (userId == null) {
            throw new MockBitException(MockbitErrorCode.ONLY_FOR_MEMBER);
        }

        Order order = orderService.findOrderById(orderId)
                .orElseThrow(() -> new MockBitException(MockbitErrorCode.NO_ORDER_RESOURCE));
        orderService.deleteOrderById(orderId);

        return ResponseEntity.ok(OrderAppResponse.from(order));
    }

    @PutMapping("/update/order_{orderId}")
    public ResponseEntity<OrderAppResponse> updateOrder(
            @PathVariable String orderId,
            @Valid @RequestBody OrderAppRequest request
    ) {
        Long userId = (Long) session.getAttribute("userId");
        Order updatedOrder = orderService.updateOrder(orderId, request, userId);

        return ResponseEntity.ok(OrderAppResponse.from(updatedOrder));
    }
}
