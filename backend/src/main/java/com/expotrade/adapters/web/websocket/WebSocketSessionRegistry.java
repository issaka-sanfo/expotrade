package com.expotrade.adapters.web.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class WebSocketSessionRegistry {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, String> sessionToUser = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> userToSessions = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> topicSubscribers = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> symbolSubscribers = new ConcurrentHashMap<>();

    public void register(String sessionId, String userId, WebSocketSession session) {
        sessions.put(sessionId, session);
        sessionToUser.put(sessionId, userId);
        userToSessions.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet()).add(sessionId);
    }

    public void remove(String sessionId) {
        sessions.remove(sessionId);
        String userId = sessionToUser.remove(sessionId);
        if (userId != null) {
            Set<String> userSessions = userToSessions.get(userId);
            if (userSessions != null) {
                userSessions.remove(sessionId);
                if (userSessions.isEmpty()) userToSessions.remove(userId);
            }
        }
        topicSubscribers.values().forEach(subs -> subs.remove(sessionId));
        symbolSubscribers.values().forEach(subs -> subs.remove(sessionId));
    }

    public void subscribe(String sessionId, String topic) {
        topicSubscribers.computeIfAbsent(topic, k -> ConcurrentHashMap.newKeySet()).add(sessionId);
    }

    public void subscribeSymbol(String sessionId, String symbol) {
        symbolSubscribers.computeIfAbsent(symbol, k -> ConcurrentHashMap.newKeySet()).add(sessionId);
    }

    public void unsubscribe(String sessionId, String topic) {
        Set<String> subs = topicSubscribers.get(topic);
        if (subs != null) subs.remove(sessionId);
    }

    public Set<WebSocketSession> getSessionsByUserId(String userId) {
        Set<String> sessionIds = userToSessions.getOrDefault(userId, Set.of());
        return sessionIds.stream()
                .map(sessions::get)
                .filter(Objects::nonNull)
                .filter(WebSocketSession::isOpen)
                .collect(Collectors.toSet());
    }

    public Set<WebSocketSession> getSessionsByTopic(String topic) {
        Set<String> sessionIds = topicSubscribers.getOrDefault(topic, Set.of());
        return sessionIds.stream()
                .map(sessions::get)
                .filter(Objects::nonNull)
                .filter(WebSocketSession::isOpen)
                .collect(Collectors.toSet());
    }

    public Set<WebSocketSession> getSessionsBySymbol(String symbol) {
        Set<String> sessionIds = symbolSubscribers.getOrDefault(symbol, Set.of());
        return sessionIds.stream()
                .map(sessions::get)
                .filter(Objects::nonNull)
                .filter(WebSocketSession::isOpen)
                .collect(Collectors.toSet());
    }

    public Set<WebSocketSession> getAllSessions() {
        return sessions.values().stream()
                .filter(WebSocketSession::isOpen)
                .collect(Collectors.toSet());
    }

    public String getUserId(String sessionId) {
        return sessionToUser.get(sessionId);
    }
}
