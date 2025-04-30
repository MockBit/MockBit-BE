package com.example.mockbit.order.application.response;

import com.example.mockbit.order.domain.Order;

import java.util.List;
import java.util.stream.Collectors;

public record PendingLimitOrders(
        List<PendingLimitOrder> orders
) {
    public static PendingLimitOrders of(List<Order> orders) {
        List<PendingLimitOrder> pendingLimitOrders = orders.stream()
                .map(PendingLimitOrder::of)
                .collect(Collectors.toList());
        return new PendingLimitOrders(pendingLimitOrders);
    }
}

