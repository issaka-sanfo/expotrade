package com.isanf.expotrade.domain.model;

import com.isanf.expotrade.domain.model.enums.BrokerType;
import com.isanf.expotrade.domain.model.enums.StrategyStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StrategyConfigTest {

    @Test
    void withStatusChangesOnlyStatus() {
        StrategyConfig config = new StrategyConfig("s1", "RSI", "RSI", List.of("AAPL"),
                BrokerType.IBKR, StrategyStatus.PAUSED, BigDecimal.TEN, BigDecimal.ONE,
                BigDecimal.TEN, BigDecimal.valueOf(20), Map.of("period", "14"), UUID.randomUUID());

        StrategyConfig active = config.withStatus(StrategyStatus.ACTIVE);

        assertEquals(StrategyStatus.ACTIVE, active.status());
        assertEquals(config.id(), active.id());
        assertEquals(config.name(), active.name());
        assertEquals(config.symbols(), active.symbols());
        assertEquals(config.parameters(), active.parameters());
    }
}
