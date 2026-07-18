package com.isanf.expotrade.application.dto;

import com.isanf.expotrade.domain.model.Order;
import com.isanf.expotrade.domain.model.enums.BrokerType;
import com.isanf.expotrade.domain.model.enums.OrderSide;
import com.isanf.expotrade.domain.model.enums.OrderStatus;
import com.isanf.expotrade.domain.model.enums.OrderType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderResponseTest {

    @Test
    void mapsPublicOrderFields() {
        Instant createdAt = Instant.parse("2026-07-18T08:00:00Z");
        Order order = new Order(
                UUID.randomUUID(),
                "AAPL",
                OrderSide.BUY,
                OrderType.LIMIT,
                BigDecimal.TEN,
                BigDecimal.valueOf(200),
                null,
                BigDecimal.valueOf(240),
                BigDecimal.valueOf(180),
                OrderStatus.PENDING,
                BrokerType.IBKR,
                "external-1",
                "strategy-1",
                UUID.randomUUID(),
                createdAt,
                createdAt
        );

        OrderResponse response = OrderResponse.from(order);

        assertEquals(order.id(), response.id());
        assertEquals("AAPL", response.symbol());
        assertEquals(OrderSide.BUY, response.side());
        assertEquals(OrderType.LIMIT, response.type());
        assertEquals(BigDecimal.TEN, response.quantity());
        assertEquals(BigDecimal.valueOf(200), response.price());
        assertEquals(BigDecimal.valueOf(180), response.stopLoss());
        assertEquals(BigDecimal.valueOf(240), response.takeProfit());
        assertEquals(OrderStatus.PENDING, response.status());
        assertEquals(BrokerType.IBKR, response.brokerType());
        assertEquals("external-1", response.externalOrderId());
        assertEquals(createdAt, response.createdAt());
    }
}
