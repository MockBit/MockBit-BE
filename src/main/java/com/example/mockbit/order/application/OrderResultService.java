package com.example.mockbit.order.application;

import com.example.mockbit.account.application.AccountService;
import com.example.mockbit.common.exception.MockBitException;
import com.example.mockbit.common.exception.MockbitErrorCode;
import com.example.mockbit.common.infrastructure.redis.RedisService;
import com.example.mockbit.order.domain.Order;
import com.example.mockbit.order.domain.OrderResult;
import com.example.mockbit.order.domain.repository.OrderResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderResultService {

    private final OrderResultRepository orderResultRepository;
    private final RedisService redisService;
    private final AccountService accountService;

    private final static String CURRENT_PRICE_KEY = "current-btc-price";

    @Value("${spring.data.redis.orders-key}")
    private String redisOrdersKey;

    public void executeOrder(String currentBtcPrice) {
        // 추후 메서드 기능 작성
        List<Order> orders =

        for (Order order : orders) {
            ((OrderResultService) AopContext.currentProxy()).processSingleOrder(order);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processSingleOrder(Order order) {
        OrderResult orderResult = OrderResult.fromOrder(order);

        try {
            accountService.completeOrder(orderResult);
            orderResultRepository.save(orderResult);
            log.info("지정가 주문이 완료되었습니다. - User: {}, Price: {}", order.getUserId(), order.getPrice());
        } catch (Exception e) {
            try {
                accountService.cancelOrder(order.getUserId(), new BigDecimal(order.getOrderPrice()));
                log.info("주문 처리 실패로 인한 환불이 완료되었습니다. - User: {}, Price: {}", order.getUserId(), order.getPrice());
            } catch (Exception refundEx) {
                log.error("환불 처리 중 오류 발생 - User: {}, Price: {}. 오류: {}", order.getUserId(), order.getPrice(), refundEx.getMessage());
            }
            log.error("지정가 주문 처리 중 오류 발생 - User: {}, Price: {}. 오류: {}", order.getUserId(), order.getPrice(), e.getMessage());
        }
    }

    @Transactional
    public OrderResult executeMarketOrder(Long userid, String orderPrice, int leverage, String position, String sellOrBuy) {
        if (sellOrBuy.equals("BUY")) {
            if (accountService.getAccountByUserId(userid).getBalance().compareTo(new BigDecimal(orderPrice)) < 0) {
                throw new MockBitException(MockbitErrorCode.NOT_ENOUGH_BALANCE);
            }
        }

        String currentBtcPrice = (String) redisService.getData(CURRENT_PRICE_KEY);

        if (currentBtcPrice == null) {
            throw new MockBitException(MockbitErrorCode.ORDER_ERROR);
        }

        OrderResult orderResult = new OrderResult(
                userid,
                currentBtcPrice,
                String.valueOf(Instant.now()),
                currentBtcPrice,
                orderPrice,
                leverage,
                position,
                sellOrBuy
        );

        try {
            orderResultRepository.save(orderResult);
            accountService.processMarketOrder(orderResult);
            log.info("현재가 주문이 완료되었습니다. - User: {}, Price: {}", userid, currentBtcPrice);
        } catch (Exception e) {
            throw new MockBitException(MockbitErrorCode.ORDER_ERROR, e);
        }

        return orderResult;
    }
}