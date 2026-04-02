package com.expotrade.infrastructure.persistence.repository;

import com.expotrade.infrastructure.persistence.entity.PositionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaPositionRepository extends JpaRepository<PositionEntity, UUID> {
    List<PositionEntity> findByUserId(UUID userId);
    Optional<PositionEntity> findByUserIdAndSymbol(UUID userId, String symbol);
}
