package com.example.mockbit.order.application;

import com.example.mockbit.common.exception.MockBitException;
import com.example.mockbit.common.exception.MockbitErrorCode;
import com.example.mockbit.common.infrastructure.redis.RedisService;
import com.example.mockbit.order.domain.Order;
import com.example.mockbit.order.domain.OrderResult;
import com.example.mockbit.order.domain.repository.OrderResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private static final String REDIS_ORDER_KEY = "Orders";

    @Transactional
    public void executeOrder(String currentStrPrice) {
        String orderPattern = REDIS_ORDER_KEY + ":" + currentStrPrice + ":*";
        Set<String> matchingOrders = redisService.getKeys(orderPattern);

        if (matchingOrders.isEmpty()) {
            log.info("{} 가격에 대한 주문 정보가 없습니다.", currentStrPrice);
            return;
        }

        List<OrderResult> orderResults = new ArrayList<>();

        for (String key : matchingOrders) {
            Order order = Optional.ofNullable((Order) redisService.getData(key))
                    .orElseThrow(() -> new MockBitException(MockbitErrorCode.ORDER_NOT_FOUND));

            orderResults.add(OrderResult.fromOrder(order));
            redisService.deleteData(key);
            log.info("지정가 주문이 완료되었습니다. - User: {}, Price: {}", order.getUserId(), order.getPrice());
        }

        try {
            orderResultRepository.saveAll(orderResults);
        } catch (Exception e) {
            throw new MockBitException(MockbitErrorCode.ORDER_ERROR, e);
        }
    }
}
