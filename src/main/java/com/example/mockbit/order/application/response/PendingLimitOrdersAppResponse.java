package com.example.mockbit.order.application.response;

import com.example.mockbit.order.domain.Order;

import java.util.List;
import java.util.stream.Collectors;

public record PendingLimitOrdersAppResponse(
        List<PendingLimitOrderAppResponse> orders
) {
    public static PendingLimitOrdersAppResponse of(List<Order> orders) {
        List<PendingLimitOrderAppResponse> pendingLimitOrderAppResponses = orders.stream()
                .map(PendingLimitOrderAppResponse::of)
                .collect(Collectors.toList());
        return new PendingLimitOrdersAppResponse(pendingLimitOrderAppResponses);
    }
}

