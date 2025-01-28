package com.example.mockbit.common.infrastructure.websocket;

import com.example.mockbit.common.infrastructure.kafka.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class TempUpbitSocketHandler extends TextWebSocketHandler {

    @Value("${spring.kafka.topic}")
    private String topic;

    private final KafkaProducerService kafkaProducerService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("Connected to Upbit WebSocket API");
        try {
            String message = "[{\"ticket\":\"UNIQUE_TICKET\"},{\"type\":\"ticker\",\"codes\":[\"KRW-BTC\"]}]";
            session.sendMessage(new TextMessage(message));
        } catch (Exception e) {
            log.error("Error sending message: {}", e.getMessage(), e);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String payload = message.getPayload();
        log.info("Received text message: {}", payload);

        kafkaProducerService.sendMessage(topic, "price", payload);
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        try {
            byte[] payload = message.getPayload().array();
            String json = new String(payload, StandardCharsets.UTF_8);
            log.info("Received binary message: {}", json);
        } catch (Exception e) {
            log.error("Error processing binary message: {}", e.getMessage(), e);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("WebSocket transport error: {}", exception.getMessage(), exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.warn("WebSocket connection closed: {}", status);
        try {
            Thread.sleep(5000);
            session.getAttributes().put("retry", true);
        } catch (Exception e) {
            log.error("Error reconnecting WebSocket: {}", e.getMessage(), e);
        }
    }
}