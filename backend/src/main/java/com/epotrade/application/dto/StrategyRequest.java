package com.epotrade.application.dto;

import com.epotrade.domain.model.enums.BrokerType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record StrategyRequest(
        @NotBlank String name, @NotBlank String type,
        @NotNull List<String> symbols, @NotNull BrokerType brokerType,
        BigDecimal maxPositionSize, BigDecimal stopLossPercent,
        BigDecimal takeProfitPercent, BigDecimal maxDrawdownPercent,
        Map<String, String> parameters
) {}
