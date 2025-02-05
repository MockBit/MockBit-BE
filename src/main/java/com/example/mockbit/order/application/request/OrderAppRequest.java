package com.example.mockbit.order.application.request;

import com.example.mockbit.order.domain.Order;
import lombok.Builder;

import java.time.Instant;

@Builder
public record OrderAppRequest(
        String price,
        String btcPrice,
        String orderPrice,
        int leverage,
        String position,
        String sellOrBuy
) {

    public Order toOrder(Long userId) {
        return Order.builder()
                .id(String.format("Orders:%s:%d", price, userId))
                .price(price)
                .userId(userId)
                .orderedAt(String.valueOf(Instant.now()))
                .btcPrice(btcPrice)
                .orderPrice(orderPrice)
                .leverage(leverage)
                .position(position)
                .sellOrBuy(sellOrBuy)
                .build();
    }
}
