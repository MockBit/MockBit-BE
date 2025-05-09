package com.example.mockbit.order.application.response;

import com.example.mockbit.order.domain.Order;

public record PendingLimitOrderAppResponse(
        String id,
        String price,
        Long userId,
        String orderedAt,
        String btcPrice,
        String orderPrice,
        int leverage,
        String position,
        String sellOrBuy
) {

    public static PendingLimitOrderAppResponse of(Order order) {
        return new PendingLimitOrderAppResponse(
                order.getId(),
                order.getPrice(),
                order.getUserId(),
                order.getOrderedAt(),
                order.getBtcPrice(),
                order.getOrderPrice(),
                order.getLeverage(),
                order.getPosition(),
                order.getSellOrBuy()
        );
    }
}
