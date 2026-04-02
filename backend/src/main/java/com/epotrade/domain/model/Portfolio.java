package com.epotrade.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

public record Portfolio(
        UUID userId,
        BigDecimal totalValue,
        BigDecimal cashBalance,
        BigDecimal unrealizedPnl,
        BigDecimal realizedPnl,
        BigDecimal dayPnl,
        BigDecimal maxDrawdown,
        List<Position> positions
) {
    public BigDecimal investedValue() {
        return totalValue.subtract(cashBalance);
    }

    public BigDecimal drawdownPercent() {
        if (totalValue.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return maxDrawdown.divide(totalValue, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }
}
