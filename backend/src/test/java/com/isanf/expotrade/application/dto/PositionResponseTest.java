package com.isanf.expotrade.application.dto;

import com.isanf.expotrade.domain.model.Position;
import com.isanf.expotrade.domain.model.enums.BrokerType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PositionResponseTest {

    @Test
    void mapsPositionFields() {
        Position position = new Position(
                UUID.randomUUID(),
                "AAPL",
                BigDecimal.TEN,
                BigDecimal.valueOf(190),
                BigDecimal.valueOf(200),
                BigDecimal.valueOf(100),
                BigDecimal.ZERO,
                BrokerType.IBKR,
                UUID.randomUUID(),
                Instant.parse("2026-07-18T08:00:00Z"),
                Instant.parse("2026-07-18T09:00:00Z")
        );

        PositionResponse response = PositionResponse.from(position);

        assertEquals(position.id(), response.id());
        assertEquals("AAPL", response.symbol());
        assertEquals(BigDecimal.TEN, response.quantity());
        assertEquals(BigDecimal.valueOf(190), response.averageEntryPrice());
        assertEquals(BigDecimal.valueOf(200), response.currentPrice());
        assertEquals(BigDecimal.valueOf(100), response.unrealizedPnl());
        assertEquals(BigDecimal.valueOf(2_000), response.marketValue());
        assertEquals(BrokerType.IBKR, response.brokerType());
    }
}
