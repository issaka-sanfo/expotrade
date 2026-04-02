package com.expotrade.domain.service;

import com.expotrade.domain.model.Order;
import com.expotrade.domain.model.Portfolio;
import com.expotrade.domain.model.StrategyConfig;

import java.math.BigDecimal;

public class RiskManager {

    public boolean validateOrder(Order order, Portfolio portfolio, StrategyConfig config) {
        return checkMaxPositionSize(order, config)
                && checkMaxDrawdown(portfolio, config)
                && checkAvailableBalance(order, portfolio);
    }

    private boolean checkMaxPositionSize(Order order, StrategyConfig config) {
        if (config.maxPositionSize() == null) return true;
        BigDecimal orderValue = order.quantity().multiply(order.price());
        return orderValue.compareTo(config.maxPositionSize()) <= 0;
    }

    private boolean checkMaxDrawdown(Portfolio portfolio, StrategyConfig config) {
        if (config.maxDrawdownPercent() == null) return true;
        return portfolio.drawdownPercent().compareTo(config.maxDrawdownPercent()) < 0;
    }

    private boolean checkAvailableBalance(Order order, Portfolio portfolio) {
        BigDecimal orderValue = order.quantity().multiply(order.price());
        return portfolio.cashBalance().compareTo(orderValue) >= 0;
    }

    public BigDecimal calculateStopLoss(BigDecimal entryPrice, BigDecimal stopLossPercent) {
        BigDecimal factor = BigDecimal.ONE.subtract(stopLossPercent.divide(BigDecimal.valueOf(100)));
        return entryPrice.multiply(factor);
    }

    public BigDecimal calculateTakeProfit(BigDecimal entryPrice, BigDecimal takeProfitPercent) {
        BigDecimal factor = BigDecimal.ONE.add(takeProfitPercent.divide(BigDecimal.valueOf(100)));
        return entryPrice.multiply(factor);
    }
}
