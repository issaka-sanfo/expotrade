package com.isanf.expotrade.infrastructure.messaging;

import com.isanf.expotrade.domain.port.out.WebSocketBroadcaster;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(KafkaEventConsumer.class);
    private final WebSocketBroadcaster broadcaster;
    private final ObjectMapper objectMapper;

    public KafkaEventConsumer(WebSocketBroadcaster broadcaster, ObjectMapper objectMapper) {
        this.broadcaster = broadcaster;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "expotrade.orders", groupId = "expotrade-websocket")
    public void onOrderEvent(ConsumerRecord<String, String> record) {
        String eventType = record.key();
        String payload = record.value();
        log.debug("Consumed order event: {} ", eventType);

        String userId = extractField(payload, "userId");
        if (userId != null) {
            broadcaster.broadcastToUser(userId, "orders", eventType, payload);
        }
    }

    @KafkaListener(topics = "expotrade.trades", groupId = "expotrade-websocket")
    public void onTradeEvent(ConsumerRecord<String, String> record) {
        String eventType = record.key();
        String payload = record.value();
        log.debug("Consumed trade event: {}", eventType);

        String userId = extractField(payload, "userId");
        if (userId != null) {
            broadcaster.broadcastToUser(userId, "trades", eventType, payload);
        }
    }

    @KafkaListener(topics = "expotrade.market-data", groupId = "expotrade-websocket")
    public void onMarketDataEvent(ConsumerRecord<String, String> record) {
        String payload = record.value();
        log.debug("Consumed market data event");

        String symbol = extractField(payload, "symbol");
        broadcaster.broadcastToSubscribers("market-data", symbol, "MARKET_DATA_UPDATE", payload);
    }

    @KafkaListener(topics = "expotrade.strategies", groupId = "expotrade-websocket")
    public void onStrategyEvent(ConsumerRecord<String, String> record) {
        String eventType = record.key();
        String payload = record.value();
        log.debug("Consumed strategy event: {}", eventType);

        String userId = extractField(payload, "userId");
        if (userId != null) {
            broadcaster.broadcastToUser(userId, "strategies", eventType, payload);
        }
    }

    private String extractField(String json, String field) {
        try {
            JsonNode node = objectMapper.readTree(json);
            JsonNode value = node.get(field);
            return value != null ? value.asText() : null;
        } catch (Exception e) {
            log.warn("Failed to extract '{}' from message: {}", field, e.getMessage());
            return null;
        }
    }
}
