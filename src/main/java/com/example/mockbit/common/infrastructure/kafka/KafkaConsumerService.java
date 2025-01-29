package com.example.mockbit.common.infrastructure.kafka;

import com.example.mockbit.common.infrastructure.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final RedisService redisService;

    @Value("${spring.data.redis.orders-key}")
    private String ordersKey;

    @KafkaListener(topics = "${spring.kafka.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeMessage(String message) {
        log.info("Consumed message from Kafka: {}", message);

        redisService.saveData(ordersKey, message);
    }
}
