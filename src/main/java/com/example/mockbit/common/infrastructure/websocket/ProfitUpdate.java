package com.example.mockbit.common.infrastructure.websocket;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProfitUpdate {
    private Long userId;
    private BigDecimal profitAmount;  // 수익 금액
    private BigDecimal profitRate;    // 수익률 (%)
    private String position;
}
