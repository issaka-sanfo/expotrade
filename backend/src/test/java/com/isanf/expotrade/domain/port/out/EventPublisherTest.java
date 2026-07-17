package com.isanf.expotrade.domain.port.out;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EventPublisherTest {

    @Test
    void exposesDomainEventPublicationOperations() throws NoSuchMethodException {
        assertThat(EventPublisher.class.getDeclaredMethod("publishOrderEvent", String.class, Object.class).getReturnType()).isEqualTo(void.class);
        assertThat(EventPublisher.class.getDeclaredMethod("publishTradeEvent", String.class, Object.class).getReturnType()).isEqualTo(void.class);
        assertThat(EventPublisher.class.getDeclaredMethod("publishMarketDataEvent", Object.class).getReturnType()).isEqualTo(void.class);
        assertThat(EventPublisher.class.getDeclaredMethod("publishStrategyEvent", String.class, Object.class).getReturnType()).isEqualTo(void.class);
    }
}
