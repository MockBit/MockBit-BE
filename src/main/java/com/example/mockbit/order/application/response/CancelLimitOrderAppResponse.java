package com.example.mockbit.order.application.response;

public record CancelLimitOrderAppResponse(
        String orderId
) {

    public static CancelLimitOrderAppResponse of(String orderId) {
        return new CancelLimitOrderAppResponse(orderId);
    }
}
