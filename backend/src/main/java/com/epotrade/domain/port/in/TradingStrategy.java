package com.epotrade.domain.port.in;

import com.epotrade.domain.model.MarketData;
import com.epotrade.domain.model.Signal;
import com.epotrade.domain.model.StrategyConfig;

import java.util.List;

public interface TradingStrategy {
    String getId();
    String getName();
    Signal generateSignal(String symbol, List<MarketData> historicalData, StrategyConfig config);
    boolean supports(String strategyType);
}
