package com.example.mockbit.order.presentation;

import com.example.mockbit.common.auth.Login;
import com.example.mockbit.common.exception.MockBitException;
import com.example.mockbit.common.exception.MockbitErrorCode;
import com.example.mockbit.order.application.OrderService;
import com.example.mockbit.order.application.request.OrderAppRequest;
import com.example.mockbit.order.application.response.OrderAppResponse;
import com.example.mockbit.order.domain.Order;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final HttpSession session;

    @PostMapping("/order")
    public ResponseEntity<OrderAppResponse> order(
            @Valid @RequestBody OrderAppRequest request
    ) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            throw new MockBitException(MockbitErrorCode.ORDER_ERROR);
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

    @GetMapping("/user_{id}")
    public ResponseEntity<List<OrderAppResponse>> getUserOrders() {
        Long userId = (Long) session.getAttribute("userId");

        List<Order> orders = orderService.findOrderByUserId(userId);

        if (orders.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(
                orders.stream().map(OrderAppResponse::from).collect(Collectors.toList())
        );
    }
}
