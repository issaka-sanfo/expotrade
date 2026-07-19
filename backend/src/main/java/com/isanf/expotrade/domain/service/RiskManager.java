package com.isanf.expotrade.domain.service;

import com.isanf.expotrade.domain.model.Order;
import com.isanf.expotrade.domain.model.Portfolio;
import com.isanf.expotrade.domain.model.StrategyConfig;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class RiskManager {

    public boolean validateOrder(Order order, Portfolio portfolio, StrategyConfig config) {
        return evaluate(order, portfolio, config).accepted();
    }

    public RiskDecision evaluate(Order order, Portfolio portfolio, StrategyConfig config) {
        if (portfolio == null) {
            return RiskDecision.rejected(RiskRejectionReason.MISSING_PORTFOLIO);
        }
        if (config == null) {
            return RiskDecision.rejected(RiskRejectionReason.MISSING_STRATEGY_CONFIG);
        }
        if (!checkMaxPositionSize(order, config)) {
            return RiskDecision.rejected(RiskRejectionReason.MAX_POSITION_SIZE_EXCEEDED);
        }
        if (!checkMaxDrawdown(portfolio, config)) {
            return RiskDecision.rejected(RiskRejectionReason.MAX_DRAWDOWN_EXCEEDED);
        }
        if (!checkAvailableBalance(order, portfolio)) {
            return RiskDecision.rejected(RiskRejectionReason.INSUFFICIENT_CASH_BALANCE);
        }
        return RiskDecision.accept();
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
        validatePositive("entryPrice", entryPrice);
        validatePositive("stopLossPercent", stopLossPercent);
        BigDecimal factor = BigDecimal.ONE.subtract(stopLossPercent.divide(BigDecimal.valueOf(100), 8, RoundingMode.HALF_UP));
        return entryPrice.multiply(factor);
    }

    public BigDecimal calculateTakeProfit(BigDecimal entryPrice, BigDecimal takeProfitPercent) {
        validatePositive("entryPrice", entryPrice);
        validatePositive("takeProfitPercent", takeProfitPercent);
        BigDecimal factor = BigDecimal.ONE.add(takeProfitPercent.divide(BigDecimal.valueOf(100), 8, RoundingMode.HALF_UP));
        return entryPrice.multiply(factor);
    }

    private void validatePositive(String field, BigDecimal value) {
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(field + " must be positive");
        }
    }
}
