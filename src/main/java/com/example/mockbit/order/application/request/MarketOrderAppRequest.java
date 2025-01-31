package com.example.mockbit.order.application.request;

import lombok.Builder;

@Builder
public record MarketOrderAppRequest(
        String orderPrice,
        int leverage,
        String position,
        String sellOrBuy
) {
}
