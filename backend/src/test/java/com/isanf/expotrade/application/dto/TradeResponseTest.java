package com.isanf.expotrade.application.dto;

import com.isanf.expotrade.domain.model.Trade;
import com.isanf.expotrade.domain.model.enums.OrderSide;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TradeResponseTest {

    @Test
    void mapsPublicTradeFields() {
        Instant executedAt = Instant.parse("2026-07-18T08:00:00Z");
        Trade trade = new Trade(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "AAPL",
                OrderSide.SELL,
                BigDecimal.valueOf(5),
                BigDecimal.valueOf(210),
                BigDecimal.ONE,
                UUID.randomUUID(),
                executedAt
        );

        TradeResponse response = TradeResponse.from(trade);

        assertEquals(trade.id(), response.id());
        assertEquals(trade.orderId(), response.orderId());
        assertEquals("AAPL", response.symbol());
        assertEquals(OrderSide.SELL, response.side());
        assertEquals(BigDecimal.valueOf(5), response.quantity());
        assertEquals(BigDecimal.valueOf(210), response.price());
        assertEquals(BigDecimal.ONE, response.commission());
        assertEquals(executedAt, response.executedAt());
    }
}
