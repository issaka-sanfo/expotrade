package com.expotrade.domain.port.in;

import com.expotrade.domain.model.MarketData;
import com.expotrade.domain.model.Signal;
import com.expotrade.domain.model.StrategyConfig;

import java.util.List;

public interface TradingStrategy {
    String getId();
    String getName();
    Signal generateSignal(String symbol, List<MarketData> historicalData, StrategyConfig config);
    boolean supports(String strategyType);
}
