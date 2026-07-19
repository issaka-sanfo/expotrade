package com.isanf.expotrade.infrastructure.persistence;

import com.isanf.expotrade.domain.model.Order;
import com.isanf.expotrade.domain.model.enums.OrderStatus;
import com.isanf.expotrade.domain.port.out.OrderRepository;
import com.isanf.expotrade.infrastructure.persistence.entity.OrderEntity;
import com.isanf.expotrade.infrastructure.persistence.repository.JpaOrderRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class OrderRepositoryAdapter implements OrderRepository {
    private final JpaOrderRepository jpaRepo;
    public OrderRepositoryAdapter(JpaOrderRepository jpaRepo) { this.jpaRepo = jpaRepo; }

    @Override public Order save(Order order) {
        return jpaRepo.save(OrderEntity.fromDomain(order)).toDomain();
    }
    @Override public Optional<Order> findById(UUID id) {
        return jpaRepo.findById(id).map(OrderEntity::toDomain);
    }
    @Override public List<Order> findByUserId(UUID userId) {
        return jpaRepo.findByUserId(userId).stream().map(OrderEntity::toDomain).toList();
    }
    @Override public List<Order> findByStatus(OrderStatus status) {
        return jpaRepo.findByStatus(status.name()).stream().map(OrderEntity::toDomain).toList();
    }
    @Override public List<Order> findByStrategyId(String strategyId) {
        return jpaRepo.findByStrategyId(strategyId).stream().map(OrderEntity::toDomain).toList();
    }
    @Override public List<Order> findByUserIdAndStrategyId(UUID userId, String strategyId) {
        return jpaRepo.findByUserIdAndStrategyId(userId, strategyId).stream().map(OrderEntity::toDomain).toList();
    }
    @Override public Optional<Order> findByExternalOrderId(String externalOrderId) {
        return jpaRepo.findByExternalOrderId(externalOrderId).map(OrderEntity::toDomain);
    }
}
