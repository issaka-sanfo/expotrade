package com.epotrade.domain.model;

import com.epotrade.domain.model.enums.BrokerType;
import com.epotrade.domain.model.enums.StrategyStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record StrategyConfig(
        String id,
        String name,
        String type,
        List<String> symbols,
        BrokerType brokerType,
        StrategyStatus status,
        BigDecimal maxPositionSize,
        BigDecimal stopLossPercent,
        BigDecimal takeProfitPercent,
        BigDecimal maxDrawdownPercent,
        Map<String, String> parameters,
        UUID userId
) {
    public StrategyConfig withStatus(StrategyStatus newStatus) {
        return new StrategyConfig(id, name, type, symbols, brokerType, newStatus,
                maxPositionSize, stopLossPercent, takeProfitPercent,
                maxDrawdownPercent, parameters, userId);
    }
}
