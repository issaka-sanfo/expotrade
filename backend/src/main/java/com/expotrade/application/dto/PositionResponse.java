package com.expotrade.application.dto;

import com.expotrade.domain.model.Position;
import com.expotrade.domain.model.enums.BrokerType;

import java.math.BigDecimal;
import java.util.UUID;

public record PositionResponse(
        UUID id, String symbol, BigDecimal quantity,
        BigDecimal averageEntryPrice, BigDecimal currentPrice,
        BigDecimal unrealizedPnl, BigDecimal marketValue, BrokerType brokerType
) {
    public static PositionResponse from(Position p) {
        return new PositionResponse(p.id(), p.symbol(), p.quantity(),
                p.averageEntryPrice(), p.currentPrice(), p.unrealizedPnl(),
                p.marketValue(), p.brokerType());
    }
}
