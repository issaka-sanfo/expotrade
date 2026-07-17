package com.isanf.expotrade.domain.model;

import com.isanf.expotrade.domain.model.enums.BrokerType;
import com.isanf.expotrade.domain.model.enums.OrderSide;
import com.isanf.expotrade.domain.model.enums.OrderStatus;
import com.isanf.expotrade.domain.model.enums.OrderType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void createBuildsPendingOrderWithGeneratedIdentity() {
        UUID userId = UUID.randomUUID();

        Order order = Order.create("AAPL", OrderSide.BUY, OrderType.LIMIT,
                BigDecimal.TEN, BigDecimal.valueOf(150), BigDecimal.valueOf(145),
                BigDecimal.valueOf(160), BrokerType.IBKR, "strategy-1", userId);

        assertNotNull(order.id());
        assertEquals("AAPL", order.symbol());
        assertEquals(OrderStatus.PENDING, order.status());
        assertEquals(BigDecimal.valueOf(160), order.takeProfitPrice());
        assertEquals(BigDecimal.valueOf(145), order.stopLossPrice());
        assertEquals(userId, order.userId());
        assertNotNull(order.createdAt());
        assertNotNull(order.updatedAt());
    }

    @Test
    void withStatusPreservesOrderDataAndUpdatesStatus() {
        Order order = sampleOrder();

        Order submitted = order.withStatus(OrderStatus.SUBMITTED);

        assertEquals(order.id(), submitted.id());
        assertEquals(order.symbol(), submitted.symbol());
        assertEquals(OrderStatus.SUBMITTED, submitted.status());
        assertTrue(!submitted.updatedAt().isBefore(order.updatedAt()));
    }

    @Test
    void withExternalOrderIdPreservesOrderDataAndSetsExternalId() {
        Order order = sampleOrder();

        Order linked = order.withExternalOrderId("ext-1");

        assertEquals(order.id(), linked.id());
        assertEquals("ext-1", linked.externalOrderId());
        assertEquals(order.status(), linked.status());
        assertTrue(!linked.updatedAt().isBefore(order.updatedAt()));
    }

    private Order sampleOrder() {
        return Order.create("AAPL", OrderSide.BUY, OrderType.LIMIT,
                BigDecimal.TEN, BigDecimal.valueOf(150), BigDecimal.valueOf(145),
                BigDecimal.valueOf(160), BrokerType.IBKR, "strategy-1", UUID.randomUUID());
    }
}
