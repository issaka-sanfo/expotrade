package com.isanf.expotrade.adapters.web.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.Map;

@Component
public class TradingWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(TradingWebSocketHandler.class);
    private final WebSocketSessionRegistry registry;
    private final ObjectMapper objectMapper;

    public TradingWebSocketHandler(WebSocketSessionRegistry registry, ObjectMapper objectMapper) {
        this.registry = registry;
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String userId = (String) session.getAttributes().get("userId");
        if (userId == null) {
            log.warn("WebSocket connection without userId, closing: {}", session.getId());
            try { session.close(CloseStatus.POLICY_VIOLATION); } catch (Exception ignored) {}
            return;
        }
        registry.register(session.getId(), userId, session);
        log.info("Trading WebSocket connected: session={}, user={}", session.getId(), userId);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Map<String, Object> request = objectMapper.readValue(message.getPayload(), Map.class);
        String action = (String) request.get("action");

        if ("subscribe".equals(action)) {
            List<String> topics = (List<String>) request.get("topics");
            List<String> symbols = (List<String>) request.get("symbols");

            if (topics != null) {
                topics.forEach(topic -> registry.subscribe(session.getId(), topic));
                log.debug("Session {} subscribed to topics: {}", session.getId(), topics);
            }
            if (symbols != null) {
                symbols.forEach(symbol -> registry.subscribeSymbol(session.getId(), symbol));
                log.debug("Session {} subscribed to symbols: {}", session.getId(), symbols);
            }
        } else if ("unsubscribe".equals(action)) {
            List<String> topics = (List<String>) request.get("topics");
            if (topics != null) {
                topics.forEach(topic -> registry.unsubscribe(session.getId(), topic));
                log.debug("Session {} unsubscribed from topics: {}", session.getId(), topics);
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        registry.remove(session.getId());
        log.info("Trading WebSocket disconnected: {} ({})", session.getId(), status);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("WebSocket transport error for session {}: {}", session.getId(), exception.getMessage());
        registry.remove(session.getId());
    }
}
