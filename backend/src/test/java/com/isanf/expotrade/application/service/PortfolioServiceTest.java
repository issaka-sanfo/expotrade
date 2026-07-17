package com.isanf.expotrade.application.service;

import com.isanf.expotrade.domain.model.MarketData;
import com.isanf.expotrade.domain.model.Portfolio;
import com.isanf.expotrade.domain.model.Position;
import com.isanf.expotrade.domain.model.enums.BrokerType;
import com.isanf.expotrade.domain.port.out.MarketDataCache;
import com.isanf.expotrade.domain.port.out.PositionRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PortfolioServiceTest {

    @Test
    void getPortfolioUpdatesPositionPricesAndTotals() {
        UUID userId = UUID.randomUUID();
        PositionRepository positionRepository = mock(PositionRepository.class);
        MarketDataCache marketDataCache = mock(MarketDataCache.class);
        Position position = new Position(UUID.randomUUID(), "AAPL", BigDecimal.TEN,
                BigDecimal.valueOf(100), BigDecimal.valueOf(100), BigDecimal.ZERO,
                BigDecimal.valueOf(25), BrokerType.IBKR, userId, Instant.now(), Instant.now());

        when(positionRepository.findByUserId(userId)).thenReturn(List.of(position));
        when(marketDataCache.getLatest("AAPL")).thenReturn(Optional.of(marketData("AAPL", 110)));

        Portfolio portfolio = new PortfolioService(positionRepository, marketDataCache)
                .getPortfolio(userId)
                .block();

        assertNotNull(portfolio);
        assertEquals(0, BigDecimal.valueOf(101100).compareTo(portfolio.totalValue()));
        assertEquals(0, BigDecimal.valueOf(100).compareTo(portfolio.unrealizedPnl()));
        assertEquals(0, BigDecimal.valueOf(25).compareTo(portfolio.realizedPnl()));
        assertEquals(0, BigDecimal.valueOf(110).compareTo(portfolio.positions().get(0).currentPrice()));
    }

    @Test
    void getPortfolioKeepsOriginalPriceWhenNoMarketDataExists() {
        UUID userId = UUID.randomUUID();
        PositionRepository positionRepository = mock(PositionRepository.class);
        MarketDataCache marketDataCache = mock(MarketDataCache.class);
        Position position = new Position(UUID.randomUUID(), "MSFT", BigDecimal.ONE,
                BigDecimal.valueOf(200), BigDecimal.valueOf(205), BigDecimal.valueOf(5),
                BigDecimal.ZERO, BrokerType.IBKR, userId, Instant.now(), Instant.now());

        when(positionRepository.findByUserId(userId)).thenReturn(List.of(position));
        when(marketDataCache.getLatest("MSFT")).thenReturn(Optional.empty());

        Portfolio portfolio = new PortfolioService(positionRepository, marketDataCache)
                .getPortfolio(userId)
                .block();

        assertNotNull(portfolio);
        assertEquals(0, BigDecimal.valueOf(100205).compareTo(portfolio.totalValue()));
        assertSame(position, portfolio.positions().get(0));
    }

    private MarketData marketData(String symbol, int last) {
        BigDecimal price = BigDecimal.valueOf(last);
        return new MarketData(symbol, price, price, price, BigDecimal.TEN,
                price, price, price, price, Instant.now());
    }
}
