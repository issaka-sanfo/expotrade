package com.epotrade.domain.port.in;

import com.epotrade.domain.model.StrategyConfig;

import java.util.List;
import java.util.UUID;

public interface ManageStrategyUseCase {
    StrategyConfig enableStrategy(String strategyId, UUID userId);
    StrategyConfig disableStrategy(String strategyId, UUID userId);
    List<StrategyConfig> getStrategies(UUID userId);
    StrategyConfig createStrategy(StrategyConfig config);
}
