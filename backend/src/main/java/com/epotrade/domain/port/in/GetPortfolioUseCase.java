package com.epotrade.domain.port.in;

import com.epotrade.domain.model.Portfolio;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface GetPortfolioUseCase {
    Mono<Portfolio> getPortfolio(UUID userId);
}
