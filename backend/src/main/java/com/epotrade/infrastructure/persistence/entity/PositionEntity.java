package com.epotrade.infrastructure.persistence.entity;

import com.epotrade.domain.model.Position;
import com.epotrade.domain.model.enums.BrokerType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "positions")
public class PositionEntity {
    @Id private UUID id;
    @Column(nullable = false) private String symbol;
    @Column(nullable = false, precision = 19, scale = 8) private BigDecimal quantity;
    @Column(nullable = false, precision = 19, scale = 8) private BigDecimal averageEntryPrice;
    @Column(precision = 19, scale = 8) private BigDecimal currentPrice;
    @Column(precision = 19, scale = 8) private BigDecimal unrealizedPnl;
    @Column(precision = 19, scale = 8) private BigDecimal realizedPnl;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private BrokerType brokerType;
    @Column(nullable = false) private UUID userId;
    @Column(nullable = false) private Instant openedAt;
    @Column(nullable = false) private Instant updatedAt;

    protected PositionEntity() {}

    public static PositionEntity fromDomain(Position p) {
        PositionEntity e = new PositionEntity();
        e.id = p.id(); e.symbol = p.symbol(); e.quantity = p.quantity();
        e.averageEntryPrice = p.averageEntryPrice(); e.currentPrice = p.currentPrice();
        e.unrealizedPnl = p.unrealizedPnl(); e.realizedPnl = p.realizedPnl();
        e.brokerType = p.brokerType(); e.userId = p.userId();
        e.openedAt = p.openedAt(); e.updatedAt = p.updatedAt();
        return e;
    }

    public Position toDomain() {
        return new Position(id, symbol, quantity, averageEntryPrice, currentPrice,
                unrealizedPnl, realizedPnl, brokerType, userId, openedAt, updatedAt);
    }
}
