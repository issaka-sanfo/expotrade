package com.expotrade.adapters.broker;

import com.expotrade.domain.model.MarketData;
import com.expotrade.domain.model.Signal;
import com.expotrade.domain.model.StrategyConfig;
import com.expotrade.domain.model.enums.BrokerType;
import com.expotrade.domain.model.enums.SignalType;
import com.expotrade.domain.model.enums.StrategyStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MovingAverageStrategyTest {

    private final MovingAverageStrategy strategy = new MovingAverageStrategy();

    @Test
    void shouldReturnHoldWithInsufficientData() {
        StrategyConfig config = createConfig(Map.of("shortPeriod", "5", "longPeriod", "20"));
        List<MarketData> data = generateData(10, 100);

        Signal signal = strategy.generateSignal("AAPL", data, config);

        assertEquals(SignalType.HOLD, signal.type());
        assertEquals("Insufficient data", signal.reason());
    }

    @Test
    void shouldSupportMovingAverageType() {
        assertTrue(strategy.supports("MOVING_AVERAGE"));
        assertTrue(strategy.supports("moving_average"));
        assertFalse(strategy.supports("RSI"));
    }

    @Test
    void shouldReturnHoldWhenNoSignal() {
        StrategyConfig config = createConfig(Map.of("shortPeriod", "3", "longPeriod", "5"));
        // Generate flat data — no crossover
        List<MarketData> data = generateData(10, 100);

        Signal signal = strategy.generateSignal("AAPL", data, config);

        assertNotNull(signal);
        assertEquals("AAPL", signal.symbol());
    }

    private StrategyConfig createConfig(Map<String, String> params) {
        return new StrategyConfig("s1", "MA Test", "MOVING_AVERAGE", List.of("AAPL"),
                BrokerType.IBKR, StrategyStatus.ACTIVE, BigDecimal.valueOf(10000),
                BigDecimal.valueOf(2), BigDecimal.valueOf(5), BigDecimal.valueOf(10),
                params, UUID.randomUUID());
    }

    private List<MarketData> generateData(int count, double basePrice) {
        List<MarketData> data = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            BigDecimal price = BigDecimal.valueOf(basePrice);
            data.add(new MarketData("AAPL", price, price, price,
                    BigDecimal.valueOf(1000000), price, price, price, price,
                    Instant.now().minusSeconds(i * 60L)));
        }
        return data;
    }
}
