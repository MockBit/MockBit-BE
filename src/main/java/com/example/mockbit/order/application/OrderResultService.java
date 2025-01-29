package com.example.mockbit.order.application;

import com.example.mockbit.common.exception.MockBitException;
import com.example.mockbit.common.exception.MockbitErrorCode;
import com.example.mockbit.common.infrastructure.redis.RedisService;
import com.example.mockbit.order.domain.Order;
import com.example.mockbit.order.domain.OrderResult;
import com.example.mockbit.order.domain.repository.OrderRepository;
import com.example.mockbit.order.domain.repository.OrderResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderResultService {

    private final OrderRepository orderRepository;
    private final OrderResultRepository orderResultRepository;
    private final RedisService redisService;

    private static final String REDIS_ORDER_KEY = "Orders";

    @Transactional
    public void executeOrder(String currentStrPrice) {
        String orderPattern = REDIS_ORDER_KEY + ":" + currentStrPrice + ":*";
        Set<String> matchingOrders = redisService.getKeys(orderPattern);

        if (matchingOrders.isEmpty()) {
            log.info("{} 가격에 대한 주문 정보가 없습니다.", currentStrPrice);
            return;
        }

        for (String key : matchingOrders) {
            Order order = Optional.ofNullable((Order) redisService.getData(key))
                    .orElseThrow(() -> new MockBitException(MockbitErrorCode.ORDER_NOT_FOUND));

            OrderResult orderResult = OrderResult.fromOrder(order);
            redisService.deleteData(key);
            try {
                OrderResult savedOrder = orderResultRepository.save(orderResult);
            } catch (Exception e) {
                throw new MockBitException(MockbitErrorCode.ORDER_ERROR, e);
            }
        }
    }
}
