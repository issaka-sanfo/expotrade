package com.epotrade.adapters.broker;

import com.epotrade.domain.model.MarketData;
import com.epotrade.domain.model.Signal;
import com.epotrade.domain.model.StrategyConfig;
import com.epotrade.domain.model.enums.SignalType;
import com.epotrade.domain.port.in.TradingStrategy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;

/**
 * RSI (Relative Strength Index) Strategy.
 * BUY when RSI drops below oversold threshold, SELL when RSI rises above overbought.
 */
@Component
public class RSIStrategy implements TradingStrategy {

    @Override
    public String getId() { return "rsi"; }

    @Override
    public String getName() { return "RSI Strategy"; }

    @Override
    public boolean supports(String strategyType) {
        return "RSI".equalsIgnoreCase(strategyType);
    }

    @Override
    public Signal generateSignal(String symbol, List<MarketData> historicalData, StrategyConfig config) {
        int period = Integer.parseInt(config.parameters().getOrDefault("period", "14"));
        double oversold = Double.parseDouble(config.parameters().getOrDefault("oversold", "30"));
        double overbought = Double.parseDouble(config.parameters().getOrDefault("overbought", "70"));

        if (historicalData.size() < period + 1) {
            return new Signal(config.id(), symbol, SignalType.HOLD, BigDecimal.ZERO,
                    null, null, null, null, "Insufficient data", Instant.now());
        }

        BigDecimal rsi = calculateRSI(historicalData, period);
        BigDecimal currentPrice = historicalData.get(0).last();
        double rsiValue = rsi.doubleValue();

        BigDecimal stopLoss = config.stopLossPercent() != null
                ? currentPrice.multiply(BigDecimal.ONE.subtract(config.stopLossPercent().divide(BigDecimal.valueOf(100))))
                : null;
        BigDecimal takeProfit = config.takeProfitPercent() != null
                ? currentPrice.multiply(BigDecimal.ONE.add(config.takeProfitPercent().divide(BigDecimal.valueOf(100))))
                : null;

        if (rsiValue < oversold) {
            BigDecimal strength = BigDecimal.valueOf((oversold - rsiValue) / oversold);
            return new Signal(config.id(), symbol, SignalType.BUY, strength,
                    currentPrice, config.maxPositionSize(), stopLoss, takeProfit,
                    String.format("RSI oversold: %.2f < %.2f", rsiValue, oversold), Instant.now());
        }

        if (rsiValue > overbought) {
            BigDecimal strength = BigDecimal.valueOf((rsiValue - overbought) / (100 - overbought));
            return new Signal(config.id(), symbol, SignalType.SELL, strength,
                    currentPrice, config.maxPositionSize(), stopLoss, takeProfit,
                    String.format("RSI overbought: %.2f > %.2f", rsiValue, overbought), Instant.now());
        }

        return new Signal(config.id(), symbol, SignalType.HOLD, BigDecimal.ZERO,
                currentPrice, null, null, null,
                String.format("RSI neutral: %.2f", rsiValue), Instant.now());
    }

    private BigDecimal calculateRSI(List<MarketData> data, int period) {
        BigDecimal avgGain = BigDecimal.ZERO;
        BigDecimal avgLoss = BigDecimal.ZERO;

        for (int i = 0; i < period; i++) {
            BigDecimal change = data.get(i).last().subtract(data.get(i + 1).last());
            if (change.compareTo(BigDecimal.ZERO) > 0) {
                avgGain = avgGain.add(change);
            } else {
                avgLoss = avgLoss.add(change.abs());
            }
        }

        avgGain = avgGain.divide(BigDecimal.valueOf(period), 6, RoundingMode.HALF_UP);
        avgLoss = avgLoss.divide(BigDecimal.valueOf(period), 6, RoundingMode.HALF_UP);

        if (avgLoss.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.valueOf(100);

        BigDecimal rs = avgGain.divide(avgLoss, 6, RoundingMode.HALF_UP);
        return BigDecimal.valueOf(100).subtract(
                BigDecimal.valueOf(100).divide(BigDecimal.ONE.add(rs), 2, RoundingMode.HALF_UP));
    }
}
