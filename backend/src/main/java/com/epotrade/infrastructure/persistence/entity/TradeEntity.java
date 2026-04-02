package com.epotrade.infrastructure.persistence.entity;

import com.epotrade.domain.model.Trade;
import com.epotrade.domain.model.enums.OrderSide;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "trades")
public class TradeEntity {
    @Id private UUID id;
    @Column(nullable = false) private UUID orderId;
    @Column(nullable = false) private String symbol;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private OrderSide side;
    @Column(nullable = false, precision = 19, scale = 8) private BigDecimal quantity;
    @Column(nullable = false, precision = 19, scale = 8) private BigDecimal price;
    @Column(precision = 19, scale = 8) private BigDecimal commission;
    @Column(nullable = false) private UUID userId;
    @Column(nullable = false) private Instant executedAt;

    protected TradeEntity() {}

    public static TradeEntity fromDomain(Trade t) {
        TradeEntity e = new TradeEntity();
        e.id = t.id(); e.orderId = t.orderId(); e.symbol = t.symbol(); e.side = t.side();
        e.quantity = t.quantity(); e.price = t.price(); e.commission = t.commission();
        e.userId = t.userId(); e.executedAt = t.executedAt();
        return e;
    }

    public Trade toDomain() {
        return new Trade(id, orderId, symbol, side, quantity, price, commission, userId, executedAt);
    }
}
