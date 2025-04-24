package com.example.mockbit.order.application.request;

public record SellLimitOrderAppRequest(
        String price,
        String btcPrice,
        String btcAmount,
        String position,
        String sellOrBuy
) {
}
