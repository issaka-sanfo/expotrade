package com.expotrade.infrastructure.messaging;

import com.expotrade.domain.port.out.EventPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaEventPublisher implements EventPublisher {

    private static final Logger log = LoggerFactory.getLogger(KafkaEventPublisher.class);
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public KafkaEventPublisher(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override public void publishOrderEvent(String eventType, Object payload) {
        publish("expotrade.orders", eventType, payload);
    }
    @Override public void publishTradeEvent(String eventType, Object payload) {
        publish("expotrade.trades", eventType, payload);
    }
    @Override public void publishMarketDataEvent(Object payload) {
        publish("expotrade.market-data", "MARKET_DATA_UPDATE", payload);
    }
    @Override public void publishStrategyEvent(String eventType, Object payload) {
        publish("expotrade.strategies", eventType, payload);
    }

    private void publish(String topic, String eventType, Object payload) {
        try {
            String message = objectMapper.writeValueAsString(payload);
            kafkaTemplate.send(topic, eventType, message)
                    .whenComplete((result, ex) -> {
                        if (ex != null) log.error("Failed to publish {} to {}: {}", eventType, topic, ex.getMessage());
                        else log.debug("Published {} to {} offset {}", eventType, topic, result.getRecordMetadata().offset());
                    });
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize event: {}", e.getMessage());
        }
    }
}
