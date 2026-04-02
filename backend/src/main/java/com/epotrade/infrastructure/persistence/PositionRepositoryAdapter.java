package com.epotrade.infrastructure.persistence;

import com.epotrade.domain.model.Position;
import com.epotrade.domain.port.out.PositionRepository;
import com.epotrade.infrastructure.persistence.entity.PositionEntity;
import com.epotrade.infrastructure.persistence.repository.JpaPositionRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class PositionRepositoryAdapter implements PositionRepository {
    private final JpaPositionRepository jpaRepo;
    public PositionRepositoryAdapter(JpaPositionRepository jpaRepo) { this.jpaRepo = jpaRepo; }

    @Override public Position save(Position p) { return jpaRepo.save(PositionEntity.fromDomain(p)).toDomain(); }
    @Override public Optional<Position> findById(UUID id) { return jpaRepo.findById(id).map(PositionEntity::toDomain); }
    @Override public List<Position> findByUserId(UUID userId) { return jpaRepo.findByUserId(userId).stream().map(PositionEntity::toDomain).toList(); }
    @Override public Optional<Position> findByUserIdAndSymbol(UUID userId, String symbol) { return jpaRepo.findByUserIdAndSymbol(userId, symbol).map(PositionEntity::toDomain); }
    @Override public void deleteById(UUID id) { jpaRepo.deleteById(id); }
}
