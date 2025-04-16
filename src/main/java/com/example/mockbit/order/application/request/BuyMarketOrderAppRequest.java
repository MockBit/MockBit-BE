package com.example.mockbit.order.application.request;

import lombok.Builder;

@Builder
public record BuyMarketOrderAppRequest(
        String orderPrice,
        int leverage,
        String position,
        String sellOrBuy
) {
}
