package com.example.mockbit.order.application.response;

import com.example.mockbit.order.domain.Order;

public record SellLimitOrderAppResponse(
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

    public static SellLimitOrderAppResponse of(Order order) {
        return new SellLimitOrderAppResponse(
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
