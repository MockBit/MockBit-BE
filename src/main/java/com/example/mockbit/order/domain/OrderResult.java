package com.example.mockbit.order.domain;

import com.example.mockbit.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "order_results")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderResult extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String price; // 주문 가격 -> 100,000,000
    private Long userId; // 주문자 ID -> 123

    private String orderedAt; // 주문 시간
    private String btcPrice; // 주문 당시 BTC 가격 -> 110,000,000
    private String orderPrice; // 주문 금액 (레버리지 적용 X) -> 1,000,000
    private int leverage; // 레버리지 -> 30
    private String position; // 숏 혹은 롱 -> Long
    private String sellOrBuy; // 구매 혹은 판매 여부 -> Buy

    public OrderResult(Long userId, String price, String orderedAt, String btcPrice, String orderPrice, int leverage, String position, String sellOrBuy) {
        this.userId = userId;
        this.price = price;
        this.orderedAt = orderedAt;
        this.btcPrice = btcPrice;
        this.orderPrice = orderPrice;
        this.leverage = leverage;
        this.position = position;
        this.sellOrBuy = sellOrBuy;
    }

    public static OrderResult fromOrder(Order order) {
        return new OrderResult(
                order.getUserId(),
                order.getPrice(),
                order.getOrderedAt(),
                order.getBtcPrice(),
                order.getOrderPrice(),
                order.getLeverage(),
                order.getPosition(),
                order.getSellOrBuy()
        );
    }
}
