package com.expotrade.domain.model;

import com.expotrade.domain.model.enums.OrderSide;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record Trade(
        UUID id,
        UUID orderId,
        String symbol,
        OrderSide side,
        BigDecimal quantity,
        BigDecimal price,
        BigDecimal commission,
        UUID userId,
        Instant executedAt
) {
    public BigDecimal totalCost() {
        return price.multiply(quantity).add(commission);
    }
}
