package com.isanf.expotrade.application.service;

import com.isanf.expotrade.domain.model.StrategyConfig;
import com.isanf.expotrade.domain.model.enums.BrokerType;
import com.isanf.expotrade.domain.model.enums.StrategyStatus;
import com.isanf.expotrade.domain.port.out.EventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StrategyServiceTest {

    private EventPublisher eventPublisher;
    private StrategyService service;

    @BeforeEach
    void setUp() {
        eventPublisher = mock(EventPublisher.class);
        service = new StrategyService(eventPublisher);
    }

    @Test
    void createStrategyAssignsIdPausesByDefaultAndPublishesEvent() {
        StrategyConfig created = service.createStrategy(config(UUID.randomUUID()));

        assertNotNull(created.id());
        assertEquals(StrategyStatus.PAUSED, created.status());
        verify(eventPublisher).publishStrategyEvent("STRATEGY_CREATED", created);
    }

    @Test
    void enableAndDisableStrategyValidateOwnerAndPublishEvents() {
        UUID userId = UUID.randomUUID();
        StrategyConfig created = service.createStrategy(config(userId));

        StrategyConfig enabled = service.enableStrategy(created.id(), userId);
        StrategyConfig disabled = service.disableStrategy(created.id(), userId);

        assertEquals(StrategyStatus.ACTIVE, enabled.status());
        assertEquals(StrategyStatus.PAUSED, disabled.status());
        verify(eventPublisher).publishStrategyEvent("STRATEGY_ENABLED", enabled);
        verify(eventPublisher).publishStrategyEvent("STRATEGY_DISABLED", disabled);
    }

    @Test
    void enableStrategyRejectsDifferentUser() {
        StrategyConfig created = service.createStrategy(config(UUID.randomUUID()));

        assertThrows(SecurityException.class, () -> service.enableStrategy(created.id(), UUID.randomUUID()));
    }

    @Test
    void getActiveStrategiesReturnsOnlyEnabledStrategies() {
        UUID userId = UUID.randomUUID();
        StrategyConfig first = service.createStrategy(config(userId));
        StrategyConfig second = service.createStrategy(config(userId));
        service.enableStrategy(first.id(), userId);

        List<StrategyConfig> activeStrategies = service.getActiveStrategies();

        assertEquals(1, activeStrategies.size());
        assertEquals(first.id(), activeStrategies.getFirst().id());
        assertFalse(activeStrategies.stream().anyMatch(strategy -> strategy.id().equals(second.id())));
    }

    private StrategyConfig config(UUID userId) {
        return new StrategyConfig("ignored", "RSI", "RSI", List.of("AAPL"),
                BrokerType.IBKR, StrategyStatus.ACTIVE, BigDecimal.valueOf(1000),
                BigDecimal.valueOf(2), BigDecimal.valueOf(5), BigDecimal.TEN,
                Map.of("period", "14"), userId);
    }
}
