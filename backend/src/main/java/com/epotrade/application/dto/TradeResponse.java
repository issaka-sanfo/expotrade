package com.epotrade.application.dto;

import com.epotrade.domain.model.Trade;
import com.epotrade.domain.model.enums.OrderSide;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TradeResponse(
        UUID id, UUID orderId, String symbol, OrderSide side,
        BigDecimal quantity, BigDecimal price, BigDecimal commission,
        Instant executedAt
) {
    public static TradeResponse from(Trade t) {
        return new TradeResponse(t.id(), t.orderId(), t.symbol(), t.side(),
                t.quantity(), t.price(), t.commission(), t.executedAt());
    }
}
