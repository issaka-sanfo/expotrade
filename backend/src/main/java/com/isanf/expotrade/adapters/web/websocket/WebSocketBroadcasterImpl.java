package com.isanf.expotrade.adapters.web.websocket;

import com.isanf.expotrade.domain.port.out.WebSocketBroadcaster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.time.Instant;
import java.util.Set;

@Component
public class WebSocketBroadcasterImpl implements WebSocketBroadcaster {

    private static final Logger log = LoggerFactory.getLogger(WebSocketBroadcasterImpl.class);
    private final WebSocketSessionRegistry registry;

    public WebSocketBroadcasterImpl(WebSocketSessionRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void broadcastToUser(String userId, String topic, String eventType, String payload) {
        String envelope = buildEnvelope(topic, eventType, payload);
        send(registry.getSessionsByUserId(userId), envelope);
    }

    @Override
    public void broadcastToAll(String topic, String eventType, String payload) {
        String envelope = buildEnvelope(topic, eventType, payload);
        send(registry.getAllSessions(), envelope);
    }

    @Override
    public void broadcastToSubscribers(String topic, String symbol, String eventType, String payload) {
        String envelope = buildEnvelope(topic, eventType, payload);
        Set<WebSocketSession> sessions = registry.getSessionsByTopic(topic);
        if (symbol != null) {
            Set<WebSocketSession> symbolSessions = registry.getSessionsBySymbol(symbol);
            sessions.retainAll(symbolSessions);
        }
        send(sessions, envelope);
    }

    private String buildEnvelope(String topic, String eventType, String payload) {
        return "{\"topic\":\"" + topic + "\",\"eventType\":\"" + eventType
                + "\",\"timestamp\":\"" + Instant.now() + "\",\"data\":" + payload + "}";
    }

    private void send(Set<WebSocketSession> sessions, String message) {
        TextMessage textMessage = new TextMessage(message);
        for (WebSocketSession session : sessions) {
            try {
                if (session.isOpen()) {
                    synchronized (session) {
                        session.sendMessage(textMessage);
                    }
                }
            } catch (Exception e) {
                log.error("Failed to send WebSocket message to session {}: {}", session.getId(), e.getMessage());
            }
        }
    }
}
