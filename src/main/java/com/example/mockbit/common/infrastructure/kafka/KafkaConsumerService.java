package com.example.mockbit.common.infrastructure.kafka;

import com.example.mockbit.account.application.BtcService;
import com.example.mockbit.common.exception.MockBitException;
import com.example.mockbit.common.exception.MockbitErrorCode;
import com.example.mockbit.common.infrastructure.redis.RedisService;
import com.example.mockbit.order.application.OrderResultService;
import com.example.mockbit.order.domain.Order;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final RedisService redisService;
    private final OrderResultService orderResultService;
    private final BtcService btcService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${spring.data.redis.current-price-key}")
    private String currentPriceKey;

    private final Map<String, List<Order>> priceOrderMap = new HashMap<>();

    @KafkaListener(topics = "${spring.kafka.topic.btc-price}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeBtcPrice(String currentBtcPrice) {
        // log.info("Consumed message from Kafka: {}", message);

        try {
            redisService.saveData(currentPriceKey, currentBtcPrice);
            BigDecimal btcPrice = new BigDecimal(currentBtcPrice);

            List<Long> userIds = btcService.getAllUserIdsWithBtc();
            for (Long userId : userIds) {
                btcService.updateProfitAndCheckLiquidation(userId, btcPrice);
            }

            List<Order> matchedOrders = priceOrderMap.getOrDefault(currentBtcPrice, new ArrayList<>());
            for (Order order : matchedOrders) {
                orderResultService.processSingleOrder(order);
                priceOrderMap.get(currentBtcPrice).remove(order);
                String redisUserOrderKey = String.format("Orders:%s", order.getUserId());
                String redisOrderDetailKey = String.format("Order_Detail:%s", order.getId());
                redisService.removeListData(redisUserOrderKey, order.getId());
                redisService.deleteData(redisOrderDetailKey);
            }
        } catch (Exception e) {
            throw new MockBitException(MockbitErrorCode.ORDER_ERROR, e);
        }
    }

    @KafkaListener(topics = "${spring.kafka.topic.create-limit-order}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeLimitOrders(String message) {
        try {
            Order order = objectMapper.readValue(message, Order.class);
            priceOrderMap.computeIfAbsent(order.getPrice(), k -> new ArrayList<>()).add(order);
        } catch (Exception e) {
            throw new MockBitException(MockbitErrorCode.ORDER_ERROR);
        }
    }

    @KafkaListener(topics = "${spring.kafka.topic.update-limit-order}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeUpdateOrder(String message) {
        try {
            Order updatedOrder = objectMapper.readValue(message, Order.class);
            List<Order> orders = priceOrderMap.get(updatedOrder.getPrice());
            if (orders != null) {
                orders.removeIf(o -> o.getId().equals(updatedOrder.getId()));
                orders.add(updatedOrder);
            }
        } catch (Exception e) {
            throw new MockBitException(MockbitErrorCode.ORDER_ERROR, e);
        }
    }

    @KafkaListener(topics = "${spring.kafka.topic.cancel-limit-order}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeCancelOrder(String message) {
        try {
            Order order = objectMapper.readValue(message, Order.class);
            List<Order> orders = priceOrderMap.get(order.getPrice());
            if (orders != null) {
                orders.removeIf(o -> o.getId().equals(order.getId()));
            }
        } catch (Exception e) {
            throw new MockBitException(MockbitErrorCode.ORDER_ERROR, e);
        }
    }
}
