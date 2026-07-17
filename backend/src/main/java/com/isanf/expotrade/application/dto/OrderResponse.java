package com.isanf.expotrade.application.dto;

import com.isanf.expotrade.domain.model.Order;
import com.isanf.expotrade.domain.model.enums.BrokerType;
import com.isanf.expotrade.domain.model.enums.OrderSide;
import com.isanf.expotrade.domain.model.enums.OrderStatus;
import com.isanf.expotrade.domain.model.enums.OrderType;

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
