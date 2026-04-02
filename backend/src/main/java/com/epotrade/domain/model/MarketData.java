package com.epotrade.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

public record MarketData(
        String symbol,
        BigDecimal bid,
        BigDecimal ask,
        BigDecimal last,
        BigDecimal volume,
        BigDecimal high,
        BigDecimal low,
        BigDecimal open,
        BigDecimal close,
        Instant timestamp
) {
    public BigDecimal spread() {
        return ask.subtract(bid);
    }

    public BigDecimal mid() {
        return bid.add(ask).divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP);
    }
}
