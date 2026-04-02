package com.epotrade.infrastructure.persistence.entity;

import com.epotrade.domain.model.Order;
import com.epotrade.domain.model.enums.*;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class OrderEntity {
    @Id private UUID id;
    @Column(nullable = false) private String symbol;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private OrderSide side;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private OrderType type;
    @Column(nullable = false, precision = 19, scale = 8) private BigDecimal quantity;
    @Column(precision = 19, scale = 8) private BigDecimal price;
    @Column(precision = 19, scale = 8) private BigDecimal stopPrice;
    @Column(precision = 19, scale = 8) private BigDecimal takeProfitPrice;
    @Column(precision = 19, scale = 8) private BigDecimal stopLossPrice;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private OrderStatus status;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private BrokerType brokerType;
    private String externalOrderId;
    private String strategyId;
    @Column(nullable = false) private UUID userId;
    @Column(nullable = false) private Instant createdAt;
    @Column(nullable = false) private Instant updatedAt;

    protected OrderEntity() {}

    public static OrderEntity fromDomain(Order o) {
        OrderEntity e = new OrderEntity();
        e.id = o.id(); e.symbol = o.symbol(); e.side = o.side(); e.type = o.type();
        e.quantity = o.quantity(); e.price = o.price(); e.stopPrice = o.stopPrice();
        e.takeProfitPrice = o.takeProfitPrice(); e.stopLossPrice = o.stopLossPrice();
        e.status = o.status(); e.brokerType = o.brokerType();
        e.externalOrderId = o.externalOrderId(); e.strategyId = o.strategyId();
        e.userId = o.userId(); e.createdAt = o.createdAt(); e.updatedAt = o.updatedAt();
        return e;
    }

    public Order toDomain() {
        return new Order(id, symbol, side, type, quantity, price, stopPrice,
                takeProfitPrice, stopLossPrice, status, brokerType,
                externalOrderId, strategyId, userId, createdAt, updatedAt);
    }
}
