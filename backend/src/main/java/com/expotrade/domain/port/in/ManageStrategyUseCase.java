package com.expotrade.domain.port.in;

import com.expotrade.domain.model.StrategyConfig;

import java.util.List;
import java.util.UUID;

public interface ManageStrategyUseCase {
    StrategyConfig enableStrategy(String strategyId, UUID userId);
    StrategyConfig disableStrategy(String strategyId, UUID userId);
    List<StrategyConfig> getStrategies(UUID userId);
    StrategyConfig createStrategy(StrategyConfig config);
}
