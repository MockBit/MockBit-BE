package com.example.mockbit.order.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.Instant;

@Data
@Builder
@RedisHash("Orders") // Redis에 저장될 keyspace 이름
public class Order implements Serializable {
    private Long id;
    private String price; // 주문 가격
    private Long userId; // 주문자 ID

    private Instant createdAt; // 주문 시간
    private String btcPrice; // 주문 당시 BTC 가격
    private String orderPrice; // 주문 금액 (레버리지 적용 X)
    private int leverage; // 레버리지
    private String position; // 숏 혹은 롱
    private String sellOrBuy; // 구매 혹은 판매 여부
}
