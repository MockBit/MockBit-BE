package com.example.mockbit.common.infrastructure.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class RealTimeProfitWebSocketHandler extends TextWebSocketHandler {

    private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userId = extractUserId(session);
        sessions.put(userId, session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String userId = extractUserId(session);
        sessions.remove(userId);
    }

    public void sendProfitUpdate(String userId, ProfitUpdate profitUpdate) throws Exception {
        WebSocketSession session = sessions.get(userId);
        if (session != null && session.isOpen()) {
            String message = objectMapper.writeValueAsString(profitUpdate);
            session.sendMessage(new TextMessage(message));
        }
    }

    public String extractUserId(WebSocketSession session) {
        String query = Objects.requireNonNull(session.getUri()).getQuery();
        if (query == null || !query.contains("=")) {
            throw new IllegalArgumentException("Invalid query format: userId is required");
        }
        String[] parts = query.split("=");
        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid query format: userId is missing");
        }
        return parts[1];
    }
}