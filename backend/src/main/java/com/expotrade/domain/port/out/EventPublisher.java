package com.expotrade.domain.port.out;

public interface EventPublisher {
    void publishOrderEvent(String eventType, Object payload);
    void publishTradeEvent(String eventType, Object payload);
    void publishMarketDataEvent(Object payload);
    void publishStrategyEvent(String eventType, Object payload);
}
