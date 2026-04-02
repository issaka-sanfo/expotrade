package com.expotrade.application.service;

import com.expotrade.domain.model.MarketData;
import com.expotrade.domain.model.Portfolio;
import com.expotrade.domain.model.Position;
import com.expotrade.domain.port.in.GetPortfolioUseCase;
import com.expotrade.domain.port.out.MarketDataCache;
import com.expotrade.domain.port.out.PositionRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class PortfolioService implements GetPortfolioUseCase {

    private final PositionRepository positionRepository;
    private final MarketDataCache marketDataCache;

    public PortfolioService(PositionRepository positionRepository, MarketDataCache marketDataCache) {
        this.positionRepository = positionRepository;
        this.marketDataCache = marketDataCache;
    }

    @Override
    public Mono<Portfolio> getPortfolio(UUID userId) {
        return Mono.fromCallable(() -> {
            List<Position> positions = positionRepository.findByUserId(userId);

            List<Position> updatedPositions = positions.stream()
                    .map(pos -> {
                        MarketData latest = marketDataCache.getLatest(pos.symbol()).orElse(null);
                        return latest != null ? pos.withCurrentPrice(latest.last()) : pos;
                    })
                    .toList();

            BigDecimal totalUnrealizedPnl = updatedPositions.stream()
                    .map(Position::unrealizedPnl).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal totalRealizedPnl = updatedPositions.stream()
                    .map(Position::realizedPnl).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal totalMarketValue = updatedPositions.stream()
                    .map(Position::marketValue).reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal cashBalance = BigDecimal.valueOf(100000);
            BigDecimal totalValue = cashBalance.add(totalMarketValue);

            return new Portfolio(userId, totalValue, cashBalance,
                    totalUnrealizedPnl, totalRealizedPnl,
                    BigDecimal.ZERO, BigDecimal.ZERO, updatedPositions);
        });
    }
}
