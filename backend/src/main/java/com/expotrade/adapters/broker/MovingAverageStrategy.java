package com.expotrade.adapters.broker;

import com.expotrade.domain.model.MarketData;
import com.expotrade.domain.model.Signal;
import com.expotrade.domain.model.StrategyConfig;
import com.expotrade.domain.model.enums.SignalType;
import com.expotrade.domain.port.in.TradingStrategy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;

/**
 * Moving Average Crossover Strategy.
 * Generates BUY when short MA crosses above long MA (golden cross),
 * SELL when short MA crosses below long MA (death cross).
 */
@Component
public class MovingAverageStrategy implements TradingStrategy {

    @Override
    public String getId() { return "moving-average"; }

    @Override
    public String getName() { return "Moving Average Crossover"; }

    @Override
    public boolean supports(String strategyType) {
        return "MOVING_AVERAGE".equalsIgnoreCase(strategyType);
    }

    @Override
    public Signal generateSignal(String symbol, List<MarketData> historicalData, StrategyConfig config) {
        int shortPeriod = Integer.parseInt(config.parameters().getOrDefault("shortPeriod", "10"));
        int longPeriod = Integer.parseInt(config.parameters().getOrDefault("longPeriod", "50"));

        if (historicalData.size() < longPeriod + 1) {
            return holdSignal(config.id(), symbol, "Insufficient data");
        }

        BigDecimal currentShortMA = calculateMA(historicalData, 0, shortPeriod);
        BigDecimal currentLongMA = calculateMA(historicalData, 0, longPeriod);
        BigDecimal prevShortMA = calculateMA(historicalData, 1, shortPeriod);
        BigDecimal prevLongMA = calculateMA(historicalData, 1, longPeriod);

        BigDecimal currentPrice = historicalData.get(0).last();
        BigDecimal stopLoss = computeLevel(currentPrice, config.stopLossPercent(), false);
        BigDecimal takeProfit = computeLevel(currentPrice, config.takeProfitPercent(), true);

        // Golden cross
        if (prevShortMA.compareTo(prevLongMA) <= 0 && currentShortMA.compareTo(currentLongMA) > 0) {
            BigDecimal strength = currentShortMA.subtract(currentLongMA)
                    .divide(currentLongMA, 4, RoundingMode.HALF_UP).abs();
            return new Signal(config.id(), symbol, SignalType.BUY, strength,
                    currentPrice, config.maxPositionSize(), stopLoss, takeProfit,
                    "Golden cross detected", Instant.now());
        }

        // Death cross
        if (prevShortMA.compareTo(prevLongMA) >= 0 && currentShortMA.compareTo(currentLongMA) < 0) {
            BigDecimal strength = currentLongMA.subtract(currentShortMA)
                    .divide(currentLongMA, 4, RoundingMode.HALF_UP).abs();
            return new Signal(config.id(), symbol, SignalType.SELL, strength,
                    currentPrice, config.maxPositionSize(), stopLoss, takeProfit,
                    "Death cross detected", Instant.now());
        }

        return holdSignal(config.id(), symbol, "No crossover");
    }

    private BigDecimal calculateMA(List<MarketData> data, int offset, int period) {
        return data.stream().skip(offset).limit(period)
                .map(MarketData::last)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(period), 4, RoundingMode.HALF_UP);
    }

    private BigDecimal computeLevel(BigDecimal price, BigDecimal percent, boolean add) {
        if (percent == null) return null;
        BigDecimal factor = add
                ? BigDecimal.ONE.add(percent.divide(BigDecimal.valueOf(100)))
                : BigDecimal.ONE.subtract(percent.divide(BigDecimal.valueOf(100)));
        return price.multiply(factor);
    }

    private Signal holdSignal(String strategyId, String symbol, String reason) {
        return new Signal(strategyId, symbol, SignalType.HOLD, BigDecimal.ZERO,
                null, null, null, null, reason, Instant.now());
    }
}
