package com.example.mockbit.order.application.response;

import com.example.mockbit.order.domain.OrderResult;

public record MarketOrderAppResponse(
        Long id,
        String price,
        Long userId,
        String orderedAt,
        String btcPrice,
        String orderPrice,
        int leverage,
        String position,
        String sellOrBuy
) {

    public static MarketOrderAppResponse from(OrderResult orderResult) {
        return new MarketOrderAppResponse(
                orderResult.getId(),
                orderResult.getPrice(),
                orderResult.getUserId(),
                orderResult.getOrderedAt(),
                orderResult.getPrice(),
                orderResult.getOrderPrice(),
                orderResult.getLeverage(),
                orderResult.getPosition(),
                orderResult.getSellOrBuy()
        );
    }
}
