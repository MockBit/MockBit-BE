package com.example.mockbit.common.infrastructure.redis;

import com.example.mockbit.common.exception.MockBitException;
import com.example.mockbit.common.exception.MockbitErrorCode;
import com.example.mockbit.order.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    public Set<String> getKeys(String orderPattern) {
        Set<String> keys = new HashSet<>();

        try {
            redisTemplate.execute((RedisConnection connection) -> {
                Cursor<byte[]> cursor = connection.scan(
                        ScanOptions.scanOptions().match(orderPattern).count(1000).build()
                );

                while (cursor.hasNext()) {
                    keys.add(new String(cursor.next(), StandardCharsets.UTF_8));
                }

                return null;
            });
        } catch (Exception e) {
            throw new MockBitException(MockbitErrorCode.INTERNAL_REDIS_ORDER_ERROR);
        }

        return keys;
    }

    public Long setGeneratedValue(String key) {
        return redisTemplate.opsForValue().increment(key, 1);
    }

    public void saveData(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void saveListData(String key, Object value) {
        redisTemplate.opsForList().leftPush(key, value);
    }

    public Object getData(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void saveOrderData(String key, Object value) {
        redisTemplate.opsForList().leftPush(key, value);
    }

    public List<Order> getOrderData(String key) {
        Long keyLength = redisTemplate.opsForValue().size(key);
        List<Order> results = new ArrayList<>();

        if (keyLength != null) {
            for (int i = 0; i < keyLength; i++) {
                Order temp = (Order) redisTemplate.opsForList().rightPop(key);
                results.add(temp);
            }
        }

        return results;
    }

    public void removeListData(String key, Object value) {
        redisTemplate.opsForList().remove(key, 1, value);
    }

    public void deleteData(String key) {
        redisTemplate.delete(key);
    }
}
