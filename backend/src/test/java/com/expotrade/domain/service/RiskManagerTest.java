package com.expotrade.domain.service;

import com.expotrade.domain.model.Order;
import com.expotrade.domain.model.Portfolio;
import com.expotrade.domain.model.StrategyConfig;
import com.expotrade.domain.model.enums.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RiskManagerTest {

    private RiskManager riskManager;
    private UUID userId;

    @BeforeEach
    void setUp() {
        riskManager = new RiskManager();
        userId = UUID.randomUUID();
    }

    @Test
    void shouldValidateOrderWithinLimits() {
        Order order = Order.create("AAPL", OrderSide.BUY, OrderType.MARKET,
                BigDecimal.valueOf(10), BigDecimal.valueOf(150),
                null, null, BrokerType.IBKR, "test-strategy", userId);

        Portfolio portfolio = new Portfolio(userId, BigDecimal.valueOf(100000),
                BigDecimal.valueOf(50000), BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.ZERO, List.of());

        StrategyConfig config = new StrategyConfig("s1", "Test", "RSI", List.of("AAPL"),
                BrokerType.IBKR, StrategyStatus.ACTIVE, BigDecimal.valueOf(5000),
                BigDecimal.valueOf(2), BigDecimal.valueOf(5), BigDecimal.valueOf(10),
                Map.of(), userId);

        assertTrue(riskManager.validateOrder(order, portfolio, config));
    }

    @Test
    void shouldRejectOrderExceedingPositionSize() {
        Order order = Order.create("AAPL", OrderSide.BUY, OrderType.MARKET,
                BigDecimal.valueOf(100), BigDecimal.valueOf(150),
                null, null, BrokerType.IBKR, "test-strategy", userId);

        Portfolio portfolio = new Portfolio(userId, BigDecimal.valueOf(100000),
                BigDecimal.valueOf(50000), BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.ZERO, List.of());

        StrategyConfig config = new StrategyConfig("s1", "Test", "RSI", List.of("AAPL"),
                BrokerType.IBKR, StrategyStatus.ACTIVE, BigDecimal.valueOf(5000),
                null, null, null, Map.of(), userId);

        assertFalse(riskManager.validateOrder(order, portfolio, config));
    }

    @Test
    void shouldCalculateStopLoss() {
        BigDecimal stopLoss = riskManager.calculateStopLoss(BigDecimal.valueOf(100), BigDecimal.valueOf(5));
        assertEquals(0, BigDecimal.valueOf(95).compareTo(stopLoss));
    }

    @Test
    void shouldCalculateTakeProfit() {
        BigDecimal takeProfit = riskManager.calculateTakeProfit(BigDecimal.valueOf(100), BigDecimal.valueOf(10));
        assertEquals(0, BigDecimal.valueOf(110).compareTo(takeProfit));
    }
}
