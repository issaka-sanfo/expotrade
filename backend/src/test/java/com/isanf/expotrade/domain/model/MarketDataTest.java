package com.isanf.expotrade.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MarketDataTest {

    @Test
    void spreadSubtractsBidFromAsk() {
        MarketData marketData = marketData(BigDecimal.valueOf(99), BigDecimal.valueOf(101));

        assertEquals(0, BigDecimal.valueOf(2).compareTo(marketData.spread()));
    }

    @Test
    void midAveragesBidAndAsk() {
        MarketData marketData = marketData(BigDecimal.valueOf(99), BigDecimal.valueOf(102));

        assertEquals(0, BigDecimal.valueOf(101).compareTo(marketData.mid()));
    }

    private MarketData marketData(BigDecimal bid, BigDecimal ask) {
        BigDecimal last = BigDecimal.valueOf(100);
        return new MarketData("AAPL", bid, ask, last, BigDecimal.TEN,
                BigDecimal.valueOf(102), BigDecimal.valueOf(98), last, last, Instant.now());
    }
}
