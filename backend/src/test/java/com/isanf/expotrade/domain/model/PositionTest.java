package com.isanf.expotrade.domain.model;

import com.isanf.expotrade.domain.model.enums.BrokerType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PositionTest {

    @Test
    void marketValueMultipliesQuantityByCurrentPrice() {
        Position position = position(BigDecimal.TEN, BigDecimal.valueOf(112), BigDecimal.ZERO);

        assertEquals(0, BigDecimal.valueOf(1120).compareTo(position.marketValue()));
    }

    @Test
    void withCurrentPriceRecalculatesUnrealizedPnlAndPreservesIdentity() {
        Position position = position(BigDecimal.TEN, BigDecimal.valueOf(100), BigDecimal.valueOf(15));

        Position updated = position.withCurrentPrice(BigDecimal.valueOf(112));

        assertEquals(position.id(), updated.id());
        assertEquals(0, BigDecimal.valueOf(112).compareTo(updated.currentPrice()));
        assertEquals(0, BigDecimal.valueOf(120).compareTo(updated.unrealizedPnl()));
        assertEquals(0, BigDecimal.valueOf(15).compareTo(updated.realizedPnl()));
        assertTrue(!updated.updatedAt().isBefore(position.updatedAt()));
    }

    private Position position(BigDecimal quantity, BigDecimal currentPrice, BigDecimal realizedPnl) {
        return new Position(UUID.randomUUID(), "AAPL", quantity, BigDecimal.valueOf(100),
                currentPrice, BigDecimal.ZERO, realizedPnl, BrokerType.IBKR,
                UUID.randomUUID(), Instant.now(), Instant.now());
    }
}
