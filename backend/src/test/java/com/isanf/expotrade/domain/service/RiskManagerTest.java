package com.isanf.expotrade.domain.service;

import com.isanf.expotrade.domain.model.Order;
import com.isanf.expotrade.domain.model.Portfolio;
import com.isanf.expotrade.domain.model.StrategyConfig;
import com.isanf.expotrade.domain.model.enums.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
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

        RiskDecision decision = riskManager.evaluate(order, portfolio, config);

        assertTrue(decision.accepted());
        assertNull(decision.rejectionReason());
        assertTrue(riskManager.validateOrder(order, portfolio, config));
    }

    @Test
    void shouldRejectOrderExceedingPositionSize() {
        Order order = order(BigDecimal.valueOf(100), BigDecimal.valueOf(150));
        Portfolio portfolio = portfolio(BigDecimal.valueOf(100000), BigDecimal.valueOf(50000), BigDecimal.ZERO);
        StrategyConfig config = config(BigDecimal.valueOf(5000), null);

        RiskDecision decision = riskManager.evaluate(order, portfolio, config);

        assertFalse(decision.accepted());
        assertEquals(RiskRejectionReason.MAX_POSITION_SIZE_EXCEEDED, decision.rejectionReason());
        assertFalse(riskManager.validateOrder(order, portfolio, config));
    }

    @Test
    void shouldAcceptOrderAtMaxPositionSizeLimit() {
        Order order = order(BigDecimal.valueOf(10), BigDecimal.valueOf(500));
        Portfolio portfolio = portfolio(BigDecimal.valueOf(100000), BigDecimal.valueOf(50000), BigDecimal.ZERO);
        StrategyConfig config = config(BigDecimal.valueOf(5000), BigDecimal.valueOf(10));

        RiskDecision decision = riskManager.evaluate(order, portfolio, config);

        assertTrue(decision.accepted());
        assertNull(decision.rejectionReason());
    }

    @Test
    void shouldRejectOrderWhenDrawdownLimitIsReached() {
        Order order = order(BigDecimal.valueOf(10), BigDecimal.valueOf(150));
        Portfolio portfolio = portfolio(BigDecimal.valueOf(100000), BigDecimal.valueOf(50000), BigDecimal.valueOf(10000));
        StrategyConfig config = config(BigDecimal.valueOf(5000), BigDecimal.valueOf(10));

        RiskDecision decision = riskManager.evaluate(order, portfolio, config);

        assertFalse(decision.accepted());
        assertEquals(RiskRejectionReason.MAX_DRAWDOWN_EXCEEDED, decision.rejectionReason());
    }

    @Test
    void shouldRejectOrderWhenCashBalanceIsInsufficient() {
        Order order = order(BigDecimal.valueOf(10), BigDecimal.valueOf(150));
        Portfolio portfolio = portfolio(BigDecimal.valueOf(100000), BigDecimal.valueOf(1000), BigDecimal.ZERO);
        StrategyConfig config = config(BigDecimal.valueOf(5000), BigDecimal.valueOf(10));

        RiskDecision decision = riskManager.evaluate(order, portfolio, config);

        assertFalse(decision.accepted());
        assertEquals(RiskRejectionReason.INSUFFICIENT_CASH_BALANCE, decision.rejectionReason());
    }

    @Test
    void shouldRejectWhenPortfolioIsMissing() {
        RiskDecision decision = riskManager.evaluate(order(BigDecimal.TEN, BigDecimal.valueOf(150)),
                null, config(BigDecimal.valueOf(5000), BigDecimal.valueOf(10)));

        assertFalse(decision.accepted());
        assertEquals(RiskRejectionReason.MISSING_PORTFOLIO, decision.rejectionReason());
    }

    @Test
    void shouldRejectWhenStrategyConfigIsMissing() {
        RiskDecision decision = riskManager.evaluate(order(BigDecimal.TEN, BigDecimal.valueOf(150)),
                portfolio(BigDecimal.valueOf(100000), BigDecimal.valueOf(50000), BigDecimal.ZERO), null);

        assertFalse(decision.accepted());
        assertEquals(RiskRejectionReason.MISSING_STRATEGY_CONFIG, decision.rejectionReason());
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

    @Test
    void shouldRejectInvalidStopLossInputs() {
        assertThrows(IllegalArgumentException.class,
                () -> riskManager.calculateStopLoss(BigDecimal.ZERO, BigDecimal.valueOf(5)));
        assertThrows(IllegalArgumentException.class,
                () -> riskManager.calculateStopLoss(BigDecimal.valueOf(100), null));
    }

    @Test
    void shouldRejectInvalidTakeProfitInputs() {
        assertThrows(IllegalArgumentException.class,
                () -> riskManager.calculateTakeProfit(null, BigDecimal.valueOf(10)));
        assertThrows(IllegalArgumentException.class,
                () -> riskManager.calculateTakeProfit(BigDecimal.valueOf(100), BigDecimal.valueOf(-1)));
    }

    private Order order(BigDecimal quantity, BigDecimal price) {
        return Order.create("AAPL", OrderSide.BUY, OrderType.MARKET,
                quantity, price, null, null, BrokerType.IBKR, "test-strategy", userId);
    }

    private Portfolio portfolio(BigDecimal totalValue, BigDecimal cashBalance, BigDecimal maxDrawdown) {
        return new Portfolio(userId, totalValue, cashBalance, BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.ZERO, maxDrawdown, List.of());
    }

    private StrategyConfig config(BigDecimal maxPositionSize, BigDecimal maxDrawdownPercent) {
        return new StrategyConfig("s1", "Test", "RSI", List.of("AAPL"),
                BrokerType.IBKR, StrategyStatus.ACTIVE, maxPositionSize,
                BigDecimal.valueOf(2), BigDecimal.valueOf(5), maxDrawdownPercent,
                Map.of(), userId);
    }
}
