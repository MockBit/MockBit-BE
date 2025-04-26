package com.example.mockbit.order.application.request;

import com.example.mockbit.common.exception.MockBitException;
import com.example.mockbit.common.exception.MockbitErrorCode;
import lombok.Builder;

@Builder
public record BuyLimitOrderAppRequest(
        String price,
        String orderPrice,
        int leverage,
        String position,
        String sellOrBuy
) {
    public BuyLimitOrderAppRequest {
        if (Integer.parseInt(orderPrice) < 5000) {
            throw new MockBitException(MockbitErrorCode.REQUEST_LIMIT);
        }
    }
}
