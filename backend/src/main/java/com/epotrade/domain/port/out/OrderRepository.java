package com.epotrade.domain.port.out;

import com.epotrade.domain.model.Order;
import com.epotrade.domain.model.enums.OrderStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(UUID id);
    List<Order> findByUserId(UUID userId);
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByStrategyId(String strategyId);
    Optional<Order> findByExternalOrderId(String externalOrderId);
}
