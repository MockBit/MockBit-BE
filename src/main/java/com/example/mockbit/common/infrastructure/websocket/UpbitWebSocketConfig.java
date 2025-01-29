package com.example.mockbit.common.infrastructure.websocket;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class UpbitWebSocketConfig {

    private static final String UPBIT_WS_URL = "wss://api.upbit.com/websocket/v1";
    private final UpbitWebSocketHandler upbitWebSocketHandler;

    @PostConstruct
    public void connect() {
        try {
            new StandardWebSocketClient().doHandshake(upbitWebSocketHandler, UPBIT_WS_URL);
            log.info("WebSocket handshake initiated.");
        } catch (Exception e) {
            log.error("WebSocket handshake failed: " + e.getMessage());
        }
    }
}

