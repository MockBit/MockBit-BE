package com.example.mockbit.order.application.response;

import com.example.mockbit.order.domain.OrderResult;

public record BuyMarketOrderAppResponse(
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

    public static BuyMarketOrderAppResponse from(OrderResult orderResult) {
        return new BuyMarketOrderAppResponse(
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
