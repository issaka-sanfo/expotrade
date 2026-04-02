package com.expotrade.domain.model;

import com.expotrade.domain.model.enums.SignalType;

import java.math.BigDecimal;
import java.time.Instant;

public record Signal(
        String strategyId,
        String symbol,
        SignalType type,
        BigDecimal strength,
        BigDecimal suggestedPrice,
        BigDecimal suggestedQuantity,
        BigDecimal stopLoss,
        BigDecimal takeProfit,
        String reason,
        Instant generatedAt
) {}
