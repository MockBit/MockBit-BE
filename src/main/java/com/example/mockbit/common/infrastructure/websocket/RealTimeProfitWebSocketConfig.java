package com.example.mockbit.common.infrastructure.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class RealTimeProfitWebSocketConfig implements WebSocketConfigurer {

    private final RealTimeProfitWebSocketHandler realTimeProfitWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(realTimeProfitWebSocketHandler, "/ws/profit").setAllowedOrigins("*");
    }
}
