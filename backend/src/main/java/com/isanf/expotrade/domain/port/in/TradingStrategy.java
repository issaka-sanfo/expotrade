package com.isanf.expotrade.domain.port.in;

import com.isanf.expotrade.domain.model.MarketData;
import com.isanf.expotrade.domain.model.Signal;
import com.isanf.expotrade.domain.model.StrategyConfig;

import java.util.List;

public interface TradingStrategy {
    String getId();
    String getName();
    Signal generateSignal(String symbol, List<MarketData> historicalData, StrategyConfig config);
    boolean supports(String strategyType);
}
