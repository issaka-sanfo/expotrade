package com.isanf.expotrade.adapters.broker;

import com.isanf.expotrade.domain.model.MarketData;
import com.isanf.expotrade.domain.model.Signal;
import com.isanf.expotrade.domain.model.StrategyConfig;
import com.isanf.expotrade.domain.model.enums.BrokerType;
import com.isanf.expotrade.domain.model.enums.SignalType;
import com.isanf.expotrade.domain.model.enums.StrategyStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RSIStrategyTest {

    private final RSIStrategy strategy = new RSIStrategy();

    @Test
    void supportsOnlyRsiStrategies() {
        assertTrue(strategy.supports("RSI"));
        assertTrue(strategy.supports("rsi"));
        assertFalse(strategy.supports("MOVING_AVERAGE"));
    }

    @Test
    void generateSignalHoldsWhenDataIsInsufficient() {
        Signal signal = strategy.generateSignal("AAPL", prices(100, 101, 102), config(Map.of("period", "14")));

        assertEquals(SignalType.HOLD, signal.type());
        assertEquals("Insufficient data", signal.reason());
    }

    @Test
    void generateSignalBuysWhenRsiIsOversold() {
        Signal signal = strategy.generateSignal("AAPL",
                prices(80, 82, 84, 86, 88, 90),
                config(Map.of("period", "5", "oversold", "30", "overbought", "70")));

        assertEquals(SignalType.BUY, signal.type());
        assertEquals(0, BigDecimal.valueOf(76).compareTo(signal.stopLoss()));
        assertEquals(0, BigDecimal.valueOf(88).compareTo(signal.takeProfit()));
    }

    @Test
    void generateSignalSellsWhenRsiIsOverbought() {
        Signal signal = strategy.generateSignal("AAPL",
                prices(110, 108, 106, 104, 102, 100),
                config(Map.of("period", "5", "oversold", "30", "overbought", "70")));

        assertEquals(SignalType.SELL, signal.type());
        assertEquals(BigDecimal.valueOf(110), signal.suggestedPrice());
    }

    private StrategyConfig config(Map<String, String> parameters) {
        return new StrategyConfig("strategy-1", "RSI", "RSI", List.of("AAPL"),
                BrokerType.IBKR, StrategyStatus.ACTIVE, BigDecimal.valueOf(1000),
                BigDecimal.valueOf(5), BigDecimal.TEN, BigDecimal.valueOf(20),
                parameters, UUID.randomUUID());
    }

    private List<MarketData> prices(int... values) {
        List<MarketData> data = new ArrayList<>();
        for (int value : values) {
            BigDecimal price = BigDecimal.valueOf(value);
            data.add(new MarketData("AAPL", price, price, price, BigDecimal.TEN,
                    price, price, price, price, Instant.now()));
        }
        return data;
    }
}
