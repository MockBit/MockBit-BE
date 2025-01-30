package com.example.mockbit.order.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.Instant;

@Data
@Builder
@RedisHash("Orders")
public class Order implements Serializable {
    private String id; // 주문 ID -> price:userId 형태
    private String price; // 주문 가격 -> 100,000,000
    private Long userId; // 주문자 ID -> 123

    private String orderedAt; // 주문 시간
    private String btcPrice; // 주문 당시 BTC 가격 -> 110,000,000
    private String orderPrice; // 주문 금액 (레버리지 적용 X) -> 1,000,000
    private int leverage; // 레버리지 -> 30
    private String position; // 숏 혹은 롱 -> Long
    private String sellOrBuy; // 구매 혹은 판매 여부 -> Buy
}
