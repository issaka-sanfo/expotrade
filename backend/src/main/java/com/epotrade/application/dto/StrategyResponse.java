package com.epotrade.application.dto;

import com.epotrade.domain.model.StrategyConfig;
import com.epotrade.domain.model.enums.BrokerType;
import com.epotrade.domain.model.enums.StrategyStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record StrategyResponse(
        String id, String name, String type, List<String> symbols,
        BrokerType brokerType, StrategyStatus status,
        BigDecimal maxPositionSize, BigDecimal stopLossPercent,
        BigDecimal takeProfitPercent, BigDecimal maxDrawdownPercent,
        Map<String, String> parameters
) {
    public static StrategyResponse from(StrategyConfig c) {
        return new StrategyResponse(c.id(), c.name(), c.type(), c.symbols(),
                c.brokerType(), c.status(), c.maxPositionSize(),
                c.stopLossPercent(), c.takeProfitPercent(),
                c.maxDrawdownPercent(), c.parameters());
    }
}
