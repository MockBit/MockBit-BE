package com.example.mockbit.common.infrastructure.websocket;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class TempUpbitWebSocketConfig {

    @Value("${spring.websocket.upbit-url}")
    private String upbitWsUrl;

    private final UpbitWebSocketHandler upbitWebSocketHandler;

    @PostConstruct
    public void connect() {
        try {
            WebSocketClient client = new StandardWebSocketClient();
            client.doHandshake(upbitWebSocketHandler, upbitWsUrl);
            log.info("WebSocket handshake initiated with URL: {}", upbitWsUrl);
        } catch (Exception e) {
            log.error("WebSocket handshake failed: {}", e.getMessage(), e);
        }
    }
}
