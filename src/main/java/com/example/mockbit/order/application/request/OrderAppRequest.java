package com.example.mockbit.order.application.request;

import lombok.Builder;

@Builder
public record OrderAppRequest(
        String price,
        String btcPrice,
        String orderPrice,
        int leverage,
        String position,
        String sellOrBuy
) {
}
