package com.isanf.expotrade.application.dto;

import com.isanf.expotrade.domain.model.Portfolio;

import java.math.BigDecimal;
import java.util.List;

public record PortfolioResponse(
        BigDecimal totalValue, BigDecimal cashBalance,
        BigDecimal unrealizedPnl, BigDecimal realizedPnl,
        BigDecimal dayPnl, BigDecimal maxDrawdown,
        List<PositionResponse> positions
) {
    public static PortfolioResponse from(Portfolio portfolio) {
        return new PortfolioResponse(
                portfolio.totalValue(), portfolio.cashBalance(),
                portfolio.unrealizedPnl(), portfolio.realizedPnl(),
                portfolio.dayPnl(), portfolio.maxDrawdown(),
                portfolio.positions().stream().map(PositionResponse::from).toList()
        );
    }
}
