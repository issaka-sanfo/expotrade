package com.expotrade.domain.port.out;

import com.expotrade.domain.model.Trade;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TradeRepository {
    Trade save(Trade trade);
    Optional<Trade> findById(UUID id);
    List<Trade> findByUserId(UUID userId);
    List<Trade> findByOrderId(UUID orderId);
    List<Trade> findBySymbolAndDateRange(String symbol, Instant from, Instant to);
}
