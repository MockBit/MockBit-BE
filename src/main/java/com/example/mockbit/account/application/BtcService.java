package com.example.mockbit.account.application;

import com.example.mockbit.account.domain.Btc;
import com.example.mockbit.account.domain.repository.BtcRepository;
import com.example.mockbit.common.exception.MockBitException;
import com.example.mockbit.common.exception.MockbitErrorCode;
import com.example.mockbit.common.infrastructure.websocket.ProfitUpdate;
import com.example.mockbit.common.infrastructure.websocket.RealTimeProfitWebSocketHandler;
import com.example.mockbit.order.domain.OrderResult;
import com.example.mockbit.order.domain.repository.OrderResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BtcService {

    private final BtcRepository btcRepository;
    private final OrderResultRepository orderResultRepository;
    private final RealTimeProfitWebSocketHandler webSocketHandler;

    @Transactional
    public void updateProfitAndCheckLiquidation(Long userId, BigDecimal currentBtcPrice) {
        Btc btc = btcRepository.findByUserId(userId)
                .orElseThrow(() -> new MockBitException(MockbitErrorCode.ACCOUNT_NOT_FOUND));
        if (btc == null || btc.getBtcBalance().compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        BigDecimal profitAmount = calculateProfit(btc, currentBtcPrice);
        BigDecimal investedAmount = btc.getBtcBalance().multiply(btc.getAvgEntryPrice());
        BigDecimal profitRate = investedAmount.compareTo(BigDecimal.ZERO) > 0
                ? profitAmount.divide(investedAmount, 8, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        try {
            ProfitUpdate profitUpdate = new ProfitUpdate();
            profitUpdate.setUserId(userId);
            profitUpdate.setProfitAmount(profitAmount);
            profitUpdate.setProfitRate(profitRate);
            profitUpdate.setPosition(btc.getPosition());
            webSocketHandler.sendProfitUpdate(userId.toString(), profitUpdate);
        } catch (Exception e) {
            log.error("WebSocket 전송 실패: {}", e.getMessage());
            throw new RuntimeException(e);
        }

        if (isLiquidationTriggered(btc, currentBtcPrice)) {
            liquidatePosition(userId, currentBtcPrice);
        }
    }

    @Transactional
    public void liquidatePosition(Long userId, BigDecimal currentBtcPrice) {
        Btc btc = btcRepository.findByUserId(userId)
                .orElseThrow(() -> new MockBitException(MockbitErrorCode.ACCOUNT_NOT_FOUND));
        if (btc == null) {
            throw new MockBitException(MockbitErrorCode.USER_NOT_FOUND);
        }
        btc.executeLiquidation();

        OrderResult liquidationResult = new OrderResult(
                userId,
                currentBtcPrice.toString(),
                Instant.now().toString(),
                currentBtcPrice.toString(),
                "NONE",
                0,
                "NONE",
                "SELL"
        );
        btcRepository.save(btc);
        orderResultRepository.save(liquidationResult);
    }

    private BigDecimal calculateProfit(Btc btc, BigDecimal currentBtcPrice) {
        BigDecimal currentValue = btc.getBtcBalance().multiply(currentBtcPrice);
        BigDecimal investedValue = btc.getBtcBalance().multiply(btc.getAvgEntryPrice());
        return "LONG".equalsIgnoreCase(btc.getPosition())
                ? currentValue.subtract(investedValue)
                : investedValue.subtract(currentValue);
    }

    private boolean isLiquidationTriggered(Btc btc, BigDecimal currentBtcPrice) {
        if (btc.getLiquidationPrice() == null) {
            return false;
        }
        if ("LONG".equalsIgnoreCase(btc.getPosition())) {
            return currentBtcPrice.compareTo(btc.getLiquidationPrice()) <= 0;
        } else if ("SHORT".equalsIgnoreCase(btc.getPosition())) {
            return currentBtcPrice.compareTo(btc.getLiquidationPrice()) >= 0;
        }
        return false;
    }

    public List<Long> getAllUserIdsWithBtc() {
        return btcRepository.findAllUserIdsWithBtcBalance();
    }
}
