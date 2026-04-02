package com.epotrade.infrastructure.persistence.repository;

import com.epotrade.infrastructure.persistence.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaOrderRepository extends JpaRepository<OrderEntity, UUID> {
    List<OrderEntity> findByUserId(UUID userId);
    List<OrderEntity> findByStatus(String status);
    List<OrderEntity> findByStrategyId(String strategyId);
    Optional<OrderEntity> findByExternalOrderId(String externalOrderId);
}
