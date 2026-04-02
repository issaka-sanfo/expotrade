package com.epotrade.infrastructure.persistence.repository;

import com.epotrade.infrastructure.persistence.entity.TradeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface JpaTradeRepository extends JpaRepository<TradeEntity, UUID> {
    List<TradeEntity> findByUserId(UUID userId);
    List<TradeEntity> findByOrderId(UUID orderId);
    List<TradeEntity> findBySymbolAndExecutedAtBetween(String symbol, Instant from, Instant to);
}
