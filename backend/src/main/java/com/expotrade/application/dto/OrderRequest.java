package com.expotrade.application.dto;

import com.expotrade.domain.model.enums.BrokerType;
import com.expotrade.domain.model.enums.OrderSide;
import com.expotrade.domain.model.enums.OrderType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record OrderRequest(
        @NotBlank String symbol,
        @NotNull OrderSide side,
        @NotNull OrderType type,
        @Positive BigDecimal quantity,
        BigDecimal price,
        BigDecimal stopLoss,
        BigDecimal takeProfit,
        @NotNull BrokerType brokerType,
        String strategyId
) {}
