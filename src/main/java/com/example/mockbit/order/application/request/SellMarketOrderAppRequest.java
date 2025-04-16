package com.example.mockbit.order.application.request;

public record SellMarketOrderAppRequest(
        String btcAmount,
        String position,
        String sellOrBuy
) {
}
