package com.example.mockbit.order.application.request;

import lombok.Builder;

@Builder
public record BuyLimitOrderAppRequest(
        String price,
        String orderPrice,
        int leverage,
        String position,
        String sellOrBuy
) {
}
