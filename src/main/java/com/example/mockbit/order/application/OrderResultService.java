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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderResultService {

    private final OrderResultRepository orderResultRepository;
    private final RedisService redisService;
    private final AccountService accountService;

    //@Value("${spring.data.redis.current-price-key}")
    //private String currentPriceKey;
    private final static String currentPriceKey = "current-btc-price";

    @Value("${spring.data.redis.orders-key}")
    private String redisOrdersKey;

    @Transactional
    public void executeOrder(String currentBtcPrice) {
        String orderPattern = redisOrdersKey + ":" + currentBtcPrice + ":*";
        Set<String> matchingOrders = redisService.getKeys(orderPattern);

        if (matchingOrders.isEmpty()) {
            log.info("{} 가격에 대한 주문 정보가 없습니다.", currentBtcPrice);
            return;
        }

        List<OrderResult> orderResults = new ArrayList<>();

        for (String key : matchingOrders) {
            Order order = Optional.ofNullable((Order) redisService.getData(key))
                    .orElseThrow(() -> new MockBitException(MockbitErrorCode.ORDER_NOT_FOUND));

            orderResults.add(OrderResult.fromOrder(order));
            accountService.completeOrder(order);
            redisService.deleteData(key);
            log.info("지정가 주문이 완료되었습니다. - User: {}, Price: {}", order.getUserId(), order.getPrice());
        }

        try {
            orderResultRepository.saveAll(orderResults);
        } catch (Exception e) {
            throw new MockBitException(MockbitErrorCode.ORDER_ERROR, e);
        }
    }

    @Transactional
    public OrderResult executeMarketOrder(Long userid, String orderPrice, int leverage, String position, String sellOrBuy) {
        String currentBtcPrice = (String) redisService.getData(currentPriceKey);

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