package com.example.mockbit.order.application;

import com.example.mockbit.account.application.AccountService;
import com.example.mockbit.account.application.BtcService;
import com.example.mockbit.common.exception.MockBitException;
import com.example.mockbit.common.exception.MockbitErrorCode;
import com.example.mockbit.common.infrastructure.kafka.KafkaProducerService;
import com.example.mockbit.common.infrastructure.redis.RedisService;
import com.example.mockbit.order.application.request.BuyLimitOrderAppRequest;
import com.example.mockbit.order.application.request.SellLimitOrderAppRequest;
import com.example.mockbit.order.application.request.UpdateOrderAppRequest;
import com.example.mockbit.order.application.response.BuyLimitOrderAppResponse;
import com.example.mockbit.order.application.response.SellLimitOrderAppResponse;
import com.example.mockbit.order.domain.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final RedisService redisService;
    private final AccountService accountService;
    private final BtcService btcService;
    private final KafkaProducerService kafkaProducerService;

    private final static String CURRENT_PRICE_KEY = "current-btc-price";
    private static final String REDIS_USER_ORDER_KEY = "Orders:%s"; // %s = userId; value = List OrderId
    private static final String REDIS_ORDER_DETAIL_KEY = "Order_Details:%s"; // %s = orderId; value = Hash Order
    private static final String KAFKA_LIMIT_ORDERS_TOPIC = "limit-orders";
    private static final String KAFKA_UPDATE_ORDERS_TOPIC = "update-limit-orders";
    private static final String KAFKA_CANCEL_ORDERS_TOPIC = "cancel-limit-orders";

    @Transactional
    public BuyLimitOrderAppResponse executeBuyLimitOrder(Long userId, BuyLimitOrderAppRequest request) {
        validateOrderPrice(request.orderPrice());
        BigDecimal orderPrice = new BigDecimal(request.orderPrice());
        if (!isBalanceEnough(userId, orderPrice)) {
            throw new MockBitException(MockbitErrorCode.NOT_ENOUGH_BALANCE);
        }

        String currentBtcPrice = (String) redisService.getData(CURRENT_PRICE_KEY);
        if (currentBtcPrice == null) {
            throw new MockBitException(MockbitErrorCode.NOT_EXISTS_CURRENT_PRICE);
        }

        String orderId = String.valueOf(redisService.setGeneratedValue("orderId"));
        String redisUserOrderKey = String.format(REDIS_USER_ORDER_KEY, userId);
        String redisOrderDetailKey = String.format(REDIS_ORDER_DETAIL_KEY, orderId);

        Order order = Order.builder()
                .id(orderId)
                .price(request.price())
                .userId(userId)
                .orderedAt(String.valueOf(Instant.now()))
                .btcPrice(currentBtcPrice)
                .orderPrice(request.orderPrice())
                .leverage(request.leverage())
                .position(request.position())
                .sellOrBuy(request.sellOrBuy())
                .build();

        redisService.saveListData(redisUserOrderKey, orderId);
        redisService.saveData(redisOrderDetailKey, order);
        kafkaProducerService.sendMessage(KAFKA_LIMIT_ORDERS_TOPIC, request.price(), order.toString());
        accountService.processOrder(userId, orderPrice);

        return BuyLimitOrderAppResponse.of(order);
    }

    @Transactional
    public SellLimitOrderAppResponse executeSellLimitOrder(Long userId, SellLimitOrderAppRequest request) {
        BigDecimal btcAmount = new BigDecimal(request.btcAmount());
        if (!isBtcEnough(userId, btcAmount)) {
            throw new MockBitException(MockbitErrorCode.NOT_ENOUGH_BTC);
        }

        String currentBtcPrice = (String) redisService.getData(CURRENT_PRICE_KEY);
        if (currentBtcPrice == null) {
            throw new MockBitException(MockbitErrorCode.NOT_EXISTS_CURRENT_PRICE);
        }

        String orderPrice = convertBtcToKRW(request.btcAmount());
        String orderId = String.valueOf(redisService.setGeneratedValue("orderId"));
        String redisUserOrderKey = String.format(REDIS_USER_ORDER_KEY, userId);
        String redisOrderDetailKey = String.format(REDIS_ORDER_DETAIL_KEY, orderId);

        Order order = Order.builder()
                .id(orderId)
                .price(request.price())
                .userId(userId)
                .orderedAt(String.valueOf(Instant.now()))
                .btcPrice(currentBtcPrice)
                .orderPrice(orderPrice)
                .leverage(1)
                .position(request.position())
                .sellOrBuy(request.sellOrBuy())
                .build();

        redisService.saveListData(redisUserOrderKey, orderId);
        redisService.saveData(redisOrderDetailKey, order);
        kafkaProducerService.sendMessage(KAFKA_LIMIT_ORDERS_TOPIC, request.price(), order.toString());
        accountService.processOrder(userId, new BigDecimal(orderPrice));

        return SellLimitOrderAppResponse.of(order);
    }

    public Optional<Order> findOrderById(String orderId) {
        String redisOrderDetailKey = String.format(REDIS_ORDER_DETAIL_KEY, orderId);
        return Optional.ofNullable((Order) redisService.getData(redisOrderDetailKey));
    }

    public List<Order> findOrderByUserId(Long userId) {
        String redisUserOrderKey = String.format(REDIS_USER_ORDER_KEY, userId);
        Set<Object> orderIds = redisService.getSetMembers(redisUserOrderKey);

        return orderIds.stream()
                .map(orderId -> String.format(REDIS_ORDER_DETAIL_KEY, orderId))
                .map(key -> (Order) redisService.getData(key))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteOrderById(String orderId) {
        String redisOrderDetailKey = String.format(REDIS_ORDER_DETAIL_KEY, orderId);
        Order order = (Order) redisService.getData(redisOrderDetailKey);
        if (order == null) {
            throw new MockBitException(MockbitErrorCode.NO_ORDER_RESOURCE);
        }

        Long userId = order.getUserId();
        String redisUserOrderKey = String.format(REDIS_USER_ORDER_KEY, userId);

        redisService.removeListData(redisUserOrderKey, orderId);
        redisService.deleteData(redisOrderDetailKey);
        kafkaProducerService.sendMessage(KAFKA_CANCEL_ORDERS_TOPIC, order.getPrice(), order.toString());
        accountService.cancelOrder(userId, new BigDecimal(order.getOrderPrice()));
    }

    @Transactional
    public Order updateOrder(String orderId, UpdateOrderAppRequest request, Long userId) {
        String redisOrderDetailKey = String.format(REDIS_ORDER_DETAIL_KEY, orderId);
        Order existingOrder = (Order) redisService.getData(redisOrderDetailKey);

        if (existingOrder == null) {
            throw new MockBitException(MockbitErrorCode.NO_ORDER_RESOURCE);
        }
        if (!Objects.equals(existingOrder.getUserId(), userId)) {
            throw new MockBitException(MockbitErrorCode.USER_ID_NOT_EQUALS_ORDER);
        }

        if (Integer.parseInt(request.orderPrice()) < 5000) {
            throw new MockBitException(MockbitErrorCode.ORDER_UNDER_MINIMUM);
        }

        accountService.cancelOrder(userId, new BigDecimal(existingOrder.getOrderPrice()));

        Order newOrder = Order.builder()
                .id(orderId)
                .price(request.price())
                .userId(userId)
                .orderedAt(String.valueOf(Instant.now()))
                .btcPrice(request.btcPrice())
                .orderPrice(request.orderPrice())
                .leverage(request.leverage())
                .position(request.position())
                .sellOrBuy(request.sellOrBuy())
                .build();

        redisService.saveData(redisOrderDetailKey, newOrder);
        kafkaProducerService.sendMessage(KAFKA_UPDATE_ORDERS_TOPIC, request.price(), newOrder.toString());
        accountService.processOrder(userId, new BigDecimal(newOrder.getOrderPrice()));

        return newOrder;
    }

    private String convertBtcToKRW(String btcAmount) {
        BigDecimal btc = new BigDecimal(btcAmount);

        BigDecimal currentBtcPrice = (BigDecimal) redisService.getData(CURRENT_PRICE_KEY);
        if (currentBtcPrice == null) {
            throw new MockBitException(MockbitErrorCode.NOT_EXISTS_CURRENT_PRICE);
        }
        BigDecimal convertedKRW = currentBtcPrice.multiply(btc);

        return String.valueOf(convertedKRW);
    }

    private boolean isBalanceEnough(Long userId, BigDecimal orderPrice) {
        return accountService.getAccountByUserId(userId).isBalanceEnough(orderPrice);
    }

    private boolean isBtcEnough(Long userId, BigDecimal btcAmount) {
        return btcService.getBtcByUserId(userId).isBtcEnough(btcAmount);
    }

    private void validateOrderPrice(String orderPrice) {
        if (Integer.parseInt(orderPrice) < 5000) {
            throw new MockBitException(MockbitErrorCode.ORDER_UNDER_MINIMUM);
        }
    }
}
