package com.example.mockbit.order.application;

import com.example.mockbit.account.application.AccountService;
import com.example.mockbit.common.exception.MockBitException;
import com.example.mockbit.common.exception.MockbitErrorCode;
import com.example.mockbit.common.infrastructure.redis.RedisService;
import com.example.mockbit.order.application.request.OrderAppRequest;
import com.example.mockbit.order.domain.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    private final AccountService accountService;

    private static final String REDIS_ORDER_KEY = "Orders:%s:%d";

    @Transactional
    public Order saveOrder(Long userId, String price, String btcPrice, String orderPrice, int leverage,
                           String position, String sellOrBuy) {
        String redisKey = String.format(REDIS_ORDER_KEY, price, userId);
        Order order = Order.builder()
                .id(redisKey)
                .price(price)
                .userId(userId)
                .orderedAt(String.valueOf(Instant.now()))
                .btcPrice(btcPrice)
                .orderPrice(orderPrice)
                .leverage(leverage)
                .position(position)
                .sellOrBuy(sellOrBuy)
                .build();

        redisService.saveData(redisKey, order);
        accountService.processOrder(userId, BigDecimal.valueOf(Long.parseLong(order.getOrderPrice())));
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
        Order order = (Order) redisService.getData(id);
        if (order == null) {
            throw new MockBitException(MockbitErrorCode.NO_ORDER_RESOURCE);
        }

        redisService.deleteData(id);

        String[] parts = id.replaceFirst("Orders:", "").split(":");

        if (parts.length < 2) {
            throw new RuntimeException("주문 ID 형식이 올바르지 않습니다.");
        }

        Long userId = Long.valueOf(parts[1]);
        accountService.cancelOrder(userId, BigDecimal.valueOf(Long.parseLong(order.getOrderPrice())));
    }

    @Transactional
    public Order updateOrder(String orderId, OrderAppRequest request, Long userId) {
        Order existingOrder = (Order) redisService.getData(orderId);

        if (existingOrder == null) {
            throw new MockBitException(MockbitErrorCode.NO_ORDER_RESOURCE);
        }
        accountService.cancelOrder(userId, BigDecimal.valueOf(Long.parseLong(existingOrder.getOrderPrice())));
        redisService.deleteData(orderId);
        String newOrderId = String.format(REDIS_ORDER_KEY, request.price(), userId);
        Order newOrder = Order.builder()
                .id(newOrderId)
                .price(request.price())
                .userId(userId)
                .orderedAt(String.valueOf(Instant.now()))
                .btcPrice(request.btcPrice())
                .orderPrice(request.orderPrice())
                .leverage(request.leverage())
                .position(request.position())
                .sellOrBuy(request.sellOrBuy())
                .build();

        redisService.saveData(newOrderId, newOrder);
        accountService.processOrder(userId, BigDecimal.valueOf(Long.parseLong(newOrder.getOrderPrice())));
        log.info("주문이 수정되었습니다. - 기존 ID: {}, 새로운 ID: {}, 가격: {}", orderId, newOrderId, request.price());

        return newOrder;
    }
}
