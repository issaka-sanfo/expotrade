package com.isanf.expotrade.domain.port.out;

import com.isanf.expotrade.domain.model.MarketData;

import java.util.List;
import java.util.Optional;

public interface MarketDataCache {
    void store(MarketData data);
    Optional<MarketData> getLatest(String symbol);
    List<MarketData> getHistory(String symbol, int limit);
}
