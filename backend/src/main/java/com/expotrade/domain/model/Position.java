package com.expotrade.domain.model;

import com.expotrade.domain.model.enums.BrokerType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record Position(
        UUID id,
        String symbol,
        BigDecimal quantity,
        BigDecimal averageEntryPrice,
        BigDecimal currentPrice,
        BigDecimal unrealizedPnl,
        BigDecimal realizedPnl,
        BrokerType brokerType,
        UUID userId,
        Instant openedAt,
        Instant updatedAt
) {
    public BigDecimal marketValue() {
        return quantity.multiply(currentPrice);
    }

    public Position withCurrentPrice(BigDecimal newPrice) {
        BigDecimal pnl = newPrice.subtract(averageEntryPrice).multiply(quantity);
        return new Position(id, symbol, quantity, averageEntryPrice, newPrice,
                pnl, realizedPnl, brokerType, userId, openedAt, Instant.now());
    }
}
