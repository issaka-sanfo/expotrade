package com.expotrade.application.service;

import com.expotrade.domain.model.StrategyConfig;
import com.expotrade.domain.model.enums.StrategyStatus;
import com.expotrade.domain.port.in.ManageStrategyUseCase;
import com.expotrade.domain.port.out.EventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class StrategyService implements ManageStrategyUseCase {

    private static final Logger log = LoggerFactory.getLogger(StrategyService.class);
    private final Map<String, StrategyConfig> strategies = new ConcurrentHashMap<>();
    private final EventPublisher eventPublisher;

    public StrategyService(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public StrategyConfig createStrategy(StrategyConfig config) {
        StrategyConfig newConfig = new StrategyConfig(
                UUID.randomUUID().toString(), config.name(), config.type(),
                config.symbols(), config.brokerType(), StrategyStatus.PAUSED,
                config.maxPositionSize(), config.stopLossPercent(),
                config.takeProfitPercent(), config.maxDrawdownPercent(),
                config.parameters(), config.userId()
        );
        strategies.put(newConfig.id(), newConfig);
        eventPublisher.publishStrategyEvent("STRATEGY_CREATED", newConfig);
        log.info("Strategy created: {} ({})", newConfig.name(), newConfig.id());
        return newConfig;
    }

    @Override
    public StrategyConfig enableStrategy(String strategyId, UUID userId) {
        StrategyConfig config = getAndValidate(strategyId, userId);
        StrategyConfig enabled = config.withStatus(StrategyStatus.ACTIVE);
        strategies.put(strategyId, enabled);
        eventPublisher.publishStrategyEvent("STRATEGY_ENABLED", enabled);
        log.info("Strategy enabled: {}", strategyId);
        return enabled;
    }

    @Override
    public StrategyConfig disableStrategy(String strategyId, UUID userId) {
        StrategyConfig config = getAndValidate(strategyId, userId);
        StrategyConfig disabled = config.withStatus(StrategyStatus.PAUSED);
        strategies.put(strategyId, disabled);
        eventPublisher.publishStrategyEvent("STRATEGY_DISABLED", disabled);
        log.info("Strategy disabled: {}", strategyId);
        return disabled;
    }

    @Override
    public List<StrategyConfig> getStrategies(UUID userId) {
        return strategies.values().stream()
                .filter(s -> s.userId().equals(userId))
                .toList();
    }

    public StrategyConfig getStrategy(String strategyId) {
        return strategies.get(strategyId);
    }

    private StrategyConfig getAndValidate(String strategyId, UUID userId) {
        StrategyConfig config = strategies.get(strategyId);
        if (config == null) throw new IllegalArgumentException("Strategy not found: " + strategyId);
        if (!config.userId().equals(userId)) throw new SecurityException("Access denied");
        return config;
    }
}
