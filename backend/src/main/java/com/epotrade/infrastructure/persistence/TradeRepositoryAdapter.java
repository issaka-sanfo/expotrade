package com.epotrade.infrastructure.persistence;

import com.epotrade.domain.model.Trade;
import com.epotrade.domain.port.out.TradeRepository;
import com.epotrade.infrastructure.persistence.entity.TradeEntity;
import com.epotrade.infrastructure.persistence.repository.JpaTradeRepository;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class TradeRepositoryAdapter implements TradeRepository {
    private final JpaTradeRepository jpaRepo;
    public TradeRepositoryAdapter(JpaTradeRepository jpaRepo) { this.jpaRepo = jpaRepo; }

    @Override public Trade save(Trade trade) {
        return jpaRepo.save(TradeEntity.fromDomain(trade)).toDomain();
    }
    @Override public Optional<Trade> findById(UUID id) {
        return jpaRepo.findById(id).map(TradeEntity::toDomain);
    }
    @Override public List<Trade> findByUserId(UUID userId) {
        return jpaRepo.findByUserId(userId).stream().map(TradeEntity::toDomain).toList();
    }
    @Override public List<Trade> findByOrderId(UUID orderId) {
        return jpaRepo.findByOrderId(orderId).stream().map(TradeEntity::toDomain).toList();
    }
    @Override public List<Trade> findBySymbolAndDateRange(String symbol, Instant from, Instant to) {
        return jpaRepo.findBySymbolAndExecutedAtBetween(symbol, from, to)
                .stream().map(TradeEntity::toDomain).toList();
    }
}
