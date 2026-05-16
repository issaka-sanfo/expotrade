package com.expotrade.domain.port.out;

public interface WebSocketBroadcaster {
    void broadcastToUser(String userId, String topic, String eventType, String payload);
    void broadcastToAll(String topic, String eventType, String payload);
    void broadcastToSubscribers(String topic, String symbol, String eventType, String payload);
}
