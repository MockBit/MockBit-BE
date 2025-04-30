package com.example.mockbit.common.infrastructure.redis;

import com.example.mockbit.common.exception.MockBitException;
import com.example.mockbit.common.exception.MockbitErrorCode;
import com.example.mockbit.order.domain.Order;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Long setGeneratedValue(String key) {
        return redisTemplate.opsForValue().increment(key, 1);
    }

    public void saveData(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void saveOrderData(String key, Object value) {
        try {
            String jsonValue = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, jsonValue);
        } catch (JsonProcessingException e) {
            throw new MockBitException(MockbitErrorCode.REDIS_SERIALIZE_ERROR, e);
        }
    }

    public void saveListData(String key, Object value) {
        redisTemplate.opsForList().leftPush(key, value);
    }

    public Object getData(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public Optional<Object> getListData(String key) {
        List<Object> orderIds = redisTemplate.opsForList().range(key, 0, -1);
        if (orderIds == null || orderIds.isEmpty()) {
            return Optional.empty();
        }

        List<Order> orders = new ArrayList<>();
        for (Object orderIdObj : orderIds) {
            String orderId = orderIdObj.toString();
            String redisOrderDetailKey = String.format("Order_Details:%s", orderId);
            String orderJson = (String) getData(redisOrderDetailKey);

            if (orderJson != null) {
                try {
                    Order order = objectMapper.readValue(orderJson, Order.class);
                    orders.add(order);
                } catch (JsonProcessingException e) {
                    throw new MockBitException(MockbitErrorCode.REDIS_DESERIALIZE_ERROR, e);
                }
            }
        }

        return Optional.of(orders);
    }

    public void removeListData(String key, Object value) {
        redisTemplate.opsForList().remove(key, 1, value);
    }

    public void deleteData(String key) {
        redisTemplate.delete(key);
    }
}
