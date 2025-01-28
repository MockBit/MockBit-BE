package com.example.mockbit.common.infrastructure.websocket;

import com.example.mockbit.common.infrastructure.kafka.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpbitWebSocketHandler extends TextWebSocketHandler {

    private final KafkaProducerService kafkaProducerService;

    @Value("{spring.kafka.topic}")
    private static String TOPIC;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String payload = message.getPayload();
        log.info("Received message from UPbit: {}", payload);

        kafkaProducerService.sendMessage(TOPIC, "price", payload);
    }
}
