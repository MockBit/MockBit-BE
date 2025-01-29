package com.example.mockbit.common.infrastructure.websocket;

import com.example.mockbit.common.infrastructure.kafka.KafkaProducerService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j
@RequiredArgsConstructor
public class UpbitWebSocketHandler extends TextWebSocketHandler {

    private final KafkaProducerService kafkaProducerService;
    private static final String UPBIT_WS_URL = "wss://api.upbit.com/websocket/v1";

    @Value("${spring.kafka.topic}")
    private String topic;

    @Value("${spring.kafka.key.current-price-key}")
    private String priceKey;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("Connected to Upbit WebSocket API");
        try {
            String message = "[{\"ticket\":\"UNIQUE_TICKET\"},{\"type\":\"ticker\",\"codes\":[\"KRW-BTC\"]}]";
            session.sendMessage(new TextMessage(message));
        } catch (Exception e) {
            log.error("Error sending message: " + e.getMessage());
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        log.info("Received text message: " + message.getPayload());
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        try {
            byte[] payload = message.getPayload().array();
            String json = new String(payload, StandardCharsets.UTF_8);
            log.info("Received binary message: " + json);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(json);
            double tradePrice = jsonNode.get("trade_price").asDouble();
            kafkaProducerService.sendMessage(topic, priceKey, String.valueOf(tradePrice));
        } catch (Exception e) {
            log.error("Error processing binary message: " + e.getMessage());
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("WebSocket transport error: " + exception.getMessage());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.error("WebSocket connection closed: {}", status);
        try {
            Thread.sleep(5000);
            new StandardWebSocketClient().doHandshake(this, UPBIT_WS_URL);
        } catch (Exception e) {
            log.error("Error reconnecting WebSocket: {}", e.getMessage());
        }
    }
}
