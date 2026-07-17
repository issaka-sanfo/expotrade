package com.isanf.expotrade.domain.port.in;

import com.isanf.expotrade.domain.model.Portfolio;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface GetPortfolioUseCase {
    Mono<Portfolio> getPortfolio(UUID userId);
}
