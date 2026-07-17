package com.isanf.expotrade.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PortfolioTest {

    @Test
    void investedValueSubtractsCashBalanceFromTotalValue() {
        Portfolio portfolio = new Portfolio(UUID.randomUUID(), BigDecimal.valueOf(125000),
                BigDecimal.valueOf(100000), BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.ZERO, List.of());

        assertEquals(0, BigDecimal.valueOf(25000).compareTo(portfolio.investedValue()));
    }

    @Test
    void drawdownPercentReturnsZeroWhenTotalValueIsZero() {
        Portfolio portfolio = new Portfolio(UUID.randomUUID(), BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.valueOf(5000), List.of());

        assertEquals(0, BigDecimal.ZERO.compareTo(portfolio.drawdownPercent()));
    }

    @Test
    void drawdownPercentCalculatesDrawdownAgainstTotalValue() {
        Portfolio portfolio = new Portfolio(UUID.randomUUID(), BigDecimal.valueOf(100000),
                BigDecimal.valueOf(80000), BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.valueOf(2500), List.of());

        assertEquals(0, BigDecimal.valueOf(2.5000).compareTo(portfolio.drawdownPercent()));
    }
}
