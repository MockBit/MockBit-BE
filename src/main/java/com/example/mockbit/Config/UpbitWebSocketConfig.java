package com.example.mockbit.Config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.TextMessage;
import java.nio.charset.StandardCharsets;

@Configuration
@Slf4j
public class UpbitWebSocketConfig {
    private static final String UPBIT_WS_URL = "wss://api.upbit.com/websocket/v1";

    @PostConstruct
    public void connect() {
        try {
            WebSocketClient client = new StandardWebSocketClient();
            client.doHandshake(new UpbitWebSocketHandler(), UPBIT_WS_URL);
            log.info("WebSocket handshake initiated.");
        } catch (Exception e) {
            log.error("WebSocket handshake failed: " + e.getMessage());
        }
    }

    private static class UpbitWebSocketHandler extends TextWebSocketHandler {
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
            log.error("WebSocket connection closed: " + status);
        }
    }
}

