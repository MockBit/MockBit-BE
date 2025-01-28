package com.example.mockbit.common.infrastructure.kafka;

import com.example.mockbit.common.infrastructure.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final RedisService redisService;

    @KafkaListener(topics = "bitcoin-price", groupId = "mockbit-group")
    public void consumeMessage(String message) {
        log.info("Consumed message from Kafka: {}", message);

        String key = "current-btc-price";
        redisService.saveData(key, message);
    }
}
