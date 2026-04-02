package com.epotrade.domain.model;

import com.epotrade.domain.model.enums.BrokerType;
import com.epotrade.domain.model.enums.OrderSide;
import com.epotrade.domain.model.enums.OrderStatus;
import com.epotrade.domain.model.enums.OrderType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record Order(
        UUID id,
        String symbol,
        OrderSide side,
        OrderType type,
        BigDecimal quantity,
        BigDecimal price,
        BigDecimal stopPrice,
        BigDecimal takeProfitPrice,
        BigDecimal stopLossPrice,
        OrderStatus status,
        BrokerType brokerType,
        String externalOrderId,
        String strategyId,
        UUID userId,
        Instant createdAt,
        Instant updatedAt
) {
    public Order withStatus(OrderStatus newStatus) {
        return new Order(id, symbol, side, type, quantity, price, stopPrice,
                takeProfitPrice, stopLossPrice, newStatus, brokerType,
                externalOrderId, strategyId, userId, createdAt, Instant.now());
    }

    public Order withExternalOrderId(String extId) {
        return new Order(id, symbol, side, type, quantity, price, stopPrice,
                takeProfitPrice, stopLossPrice, status, brokerType,
                extId, strategyId, userId, createdAt, Instant.now());
    }

    public static Order create(String symbol, OrderSide side, OrderType type,
                               BigDecimal quantity, BigDecimal price,
                               BigDecimal stopLoss, BigDecimal takeProfit,
                               BrokerType brokerType, String strategyId, UUID userId) {
        return new Order(
                UUID.randomUUID(), symbol, side, type, quantity, price, null,
                takeProfit, stopLoss, OrderStatus.PENDING, brokerType,
                null, strategyId, userId, Instant.now(), Instant.now()
        );
    }
}
