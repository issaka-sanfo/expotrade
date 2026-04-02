package com.epotrade.application.dto;

import com.epotrade.domain.model.Order;
import com.epotrade.domain.model.enums.BrokerType;
import com.epotrade.domain.model.enums.OrderSide;
import com.epotrade.domain.model.enums.OrderStatus;
import com.epotrade.domain.model.enums.OrderType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderResponse(
        UUID id, String symbol, OrderSide side, OrderType type,
        BigDecimal quantity, BigDecimal price, BigDecimal stopLoss,
        BigDecimal takeProfit, OrderStatus status, BrokerType brokerType,
        String externalOrderId, Instant createdAt
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.id(), order.symbol(), order.side(), order.type(),
                order.quantity(), order.price(), order.stopLossPrice(),
                order.takeProfitPrice(), order.status(), order.brokerType(),
                order.externalOrderId(), order.createdAt()
        );
    }
}
