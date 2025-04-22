package com.example.mockbit.order.application;

import com.example.mockbit.account.application.AccountService;
import com.example.mockbit.account.application.BtcService;
import com.example.mockbit.common.exception.MockBitException;
import com.example.mockbit.common.exception.MockbitErrorCode;
import com.example.mockbit.common.infrastructure.redis.RedisService;
import com.example.mockbit.order.application.request.BuyMarketOrderAppRequest;
import com.example.mockbit.order.application.request.SellMarketOrderAppRequest;
import com.example.mockbit.order.application.response.BuyMarketOrderAppResponse;
import com.example.mockbit.order.application.response.SellMarketOrderAppResponse;
import com.example.mockbit.order.domain.Order;
import com.example.mockbit.order.domain.OrderResult;
import com.example.mockbit.order.domain.repository.OrderResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderResultService {

    private final OrderResultRepository orderResultRepository;
    private final RedisService redisService;
    private final AccountService accountService;
    private final BtcService btcService;

    private final static String CURRENT_PRICE_KEY = "current-btc-price";

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processSingleOrder(Order order) {
        OrderResult orderResult = OrderResult.fromOrder(order);
        try {
            accountService.completeOrder(orderResult);
            orderResultRepository.save(orderResult);
            log.info("지정가 주문이 완료되었습니다. - User: {}, Price: {}", order.getUserId(), order.getPrice());
        } catch (Exception e) {
            accountService.cancelOrder(order.getUserId(), new BigDecimal(order.getOrderPrice()));
            log.error("지정가 주문 처리 실패 - User: {}, Price: {}, Error: {}", order.getUserId(), order.getPrice(), e.getMessage());
            throw new MockBitException(MockbitErrorCode.MARKET_ORDER_ERROR, e);
        }
    }

    @Transactional
    public BuyMarketOrderAppResponse executeBuyMarketOrder(Long userId, BuyMarketOrderAppRequest request) {
        if (Integer.parseInt(request.orderPrice()) < 5000) {
            throw new MockBitException(MockbitErrorCode.ORDER_UNDER_MINIMUM);
        }

        if (!accountService.getAccountByUserId(userId).isBalanceEnough(new BigDecimal(request.orderPrice()))) {
            throw new MockBitException(MockbitErrorCode.NOT_ENOUGH_BALANCE);
        }

        String currentBtcPrice = (String) redisService.getData(CURRENT_PRICE_KEY);

        if (currentBtcPrice == null) {
            throw new MockBitException(MockbitErrorCode.NOT_EXISTS_CURRENT_PRICE);
        }

        OrderResult orderResult = new OrderResult(
                userId,
                currentBtcPrice,
                String.valueOf(Instant.now()),
                currentBtcPrice,
                request.orderPrice(),
                request.leverage(),
                request.position(),
                request.sellOrBuy()
        );

        try {
            orderResultRepository.save(orderResult);
            accountService.processMarketOrder(orderResult);
            log.info("현재가 주문 구매가 완료되었습니다. - User: {}, Price: {}", userId, currentBtcPrice);
        } catch (Exception e) {
            throw new MockBitException(MockbitErrorCode.MARKET_ORDER_ERROR, e);
        }

        return BuyMarketOrderAppResponse.of(orderResult);
    }

    @Transactional
    public SellMarketOrderAppResponse executeSellMarketOrder(Long userId, SellMarketOrderAppRequest request) {

        if (!btcService.getBtcByUserId(userId).isBtcEnough(new BigDecimal(request.btcAmount()))) {
            throw new MockBitException(MockbitErrorCode.NOT_ENOUGH_BTC);
        }

        String currentBtcPrice = (String) redisService.getData(CURRENT_PRICE_KEY);

        if (currentBtcPrice == null) {
            throw new MockBitException(MockbitErrorCode.NOT_EXISTS_CURRENT_PRICE);
        }

        String orderPrice = convertBtcToKRW(request.btcAmount());

        OrderResult orderResult = new OrderResult(
                userId,
                currentBtcPrice,
                String.valueOf(Instant.now()),
                currentBtcPrice,
                orderPrice,
                0,
                request.position(),
                request.sellOrBuy()
        );

        try {
            orderResultRepository.save(orderResult);
            accountService.processMarketOrder(orderResult);
            log.info("현재가 주문 구매가 완료되었습니다. - User: {}, Price: {}", userId, currentBtcPrice);
        } catch (Exception e) {
            throw new MockBitException(MockbitErrorCode.MARKET_ORDER_ERROR, e);
        }

        return SellMarketOrderAppResponse.of(orderResult);
    }

    public String convertBtcToKRW(String btcAmount) {
        BigDecimal btc = new BigDecimal(btcAmount);

        return String.valueOf(btc);
    }
}