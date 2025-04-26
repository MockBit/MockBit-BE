package com.example.mockbit.common.infrastructure.redis;

import com.example.mockbit.common.exception.MockBitException;
import com.example.mockbit.common.exception.MockbitErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Set<Object> getSetMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

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

    public void removeListData(String key, Object value) {
        redisTemplate.opsForList().remove(key, 1, value);
    }

    public void deleteData(String key) {
        redisTemplate.delete(key);
    }
}
