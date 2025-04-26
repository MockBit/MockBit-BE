package com.example.mockbit.order.application.request;

import com.example.mockbit.common.exception.MockBitException;
import com.example.mockbit.common.exception.MockbitErrorCode;
import lombok.Builder;

@Builder
public record UpdateOrderAppRequest(
        String price,
        String btcPrice,
        String orderPrice,
        int leverage,
        String position,
        String sellOrBuy
) {
    public UpdateOrderAppRequest {
        if (Integer.parseInt(orderPrice) < 5000) {
            throw new MockBitException(MockbitErrorCode.REQUEST_LIMIT);
        }
    }
}
