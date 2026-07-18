package com.isanf.expotrade.application.dto;

import com.isanf.expotrade.domain.model.Portfolio;
import com.isanf.expotrade.domain.model.Position;
import com.isanf.expotrade.domain.model.enums.BrokerType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PortfolioResponseTest {

    @Test
    void mapsPortfolioFieldsAndNestedPositions() {
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
        Portfolio portfolio = new Portfolio(
                UUID.randomUUID(),
                BigDecimal.valueOf(10_000),
                BigDecimal.valueOf(8_000),
                BigDecimal.valueOf(100),
                BigDecimal.ZERO,
                BigDecimal.valueOf(25),
                BigDecimal.valueOf(300),
                List.of(position)
        );

        PortfolioResponse response = PortfolioResponse.from(portfolio);

        assertEquals(portfolio.totalValue(), response.totalValue());
        assertEquals(portfolio.cashBalance(), response.cashBalance());
        assertEquals(portfolio.unrealizedPnl(), response.unrealizedPnl());
        assertEquals(portfolio.realizedPnl(), response.realizedPnl());
        assertEquals(portfolio.dayPnl(), response.dayPnl());
        assertEquals(portfolio.maxDrawdown(), response.maxDrawdown());
        assertEquals(1, response.positions().size());
        assertEquals(position.id(), response.positions().getFirst().id());
    }
}
