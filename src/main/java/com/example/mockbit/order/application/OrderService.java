package com.example.mockbit.order.application;

import com.example.mockbit.common.infrastructure.redis.RedisService;
import com.example.mockbit.order.domain.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final RedisService redisService;

    private static final String REDIS_ORDER_KEY = "Orders:%s:%d";

    @Transactional
    public Order saveOrder(Long userId, String price, String btcPrice, String orderPrice, int leverage,
                           String position, String sellOrBuy) {
        Order order = Order.builder()
                .id(String.format("%s:%d", price, userId))
                .price(price)
                .userId(userId)
                .orderedAt(String.valueOf(Instant.now()))
                .btcPrice(btcPrice)
                .orderPrice(orderPrice)
                .leverage(leverage)
                .position(position)
                .sellOrBuy(sellOrBuy)
                .build();

        String redisKey = String.format(REDIS_ORDER_KEY, price, userId);
        redisService.saveData(redisKey, String.valueOf(order));
        log.info("지정가 주문이 등록되었습니다. - User: {}, Price: {}", userId, price);

        return order;
    }

    @Transactional(readOnly = true)
    public Optional<Order> findOrderById(String id) {
        return Optional.ofNullable((Order) redisService.getData(id));
    }

    @Transactional(readOnly = true)
    public List<Order> findOrderByUserId(Long userId) {
        String pattern = String.format(REDIS_ORDER_KEY, "*", userId);
        Set<String> orderKeys = redisService.getKeys(pattern);

        return orderKeys.stream()
                .map(key -> (Order) redisService.getData(key))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteOrderById(String id) {
        redisService.deleteData(id);
    }
}
