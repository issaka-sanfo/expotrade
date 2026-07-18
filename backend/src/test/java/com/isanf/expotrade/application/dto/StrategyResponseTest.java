package com.isanf.expotrade.application.dto;

import com.isanf.expotrade.domain.model.StrategyConfig;
import com.isanf.expotrade.domain.model.enums.BrokerType;
import com.isanf.expotrade.domain.model.enums.StrategyStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StrategyResponseTest {

    @Test
    void mapsStrategyConfigFields() {
        StrategyConfig config = new StrategyConfig(
                "strategy-1",
                "Momentum",
                "MOVING_AVERAGE",
                List.of("AAPL", "MSFT"),
                BrokerType.IBKR,
                StrategyStatus.ACTIVE,
                BigDecimal.valueOf(1_000),
                BigDecimal.valueOf(2),
                BigDecimal.valueOf(4),
                BigDecimal.valueOf(10),
                Map.of("window", "20"),
                UUID.randomUUID()
        );

        StrategyResponse response = StrategyResponse.from(config);

        assertEquals(config.id(), response.id());
        assertEquals(config.name(), response.name());
        assertEquals(config.type(), response.type());
        assertEquals(config.symbols(), response.symbols());
        assertEquals(config.brokerType(), response.brokerType());
        assertEquals(config.status(), response.status());
        assertEquals(config.maxPositionSize(), response.maxPositionSize());
        assertEquals(config.stopLossPercent(), response.stopLossPercent());
        assertEquals(config.takeProfitPercent(), response.takeProfitPercent());
        assertEquals(config.maxDrawdownPercent(), response.maxDrawdownPercent());
        assertEquals(config.parameters(), response.parameters());
    }
}
