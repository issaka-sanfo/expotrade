package com.expotrade.domain.model;

import com.expotrade.domain.model.enums.BrokerAccountStatus;
import com.expotrade.domain.model.enums.BrokerType;
import com.expotrade.domain.model.enums.OrderSide;
import com.expotrade.domain.model.enums.OrderStatus;
import com.expotrade.domain.model.enums.OrderType;
import com.expotrade.domain.model.enums.StrategyStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DomainModelTest {

    @Test
    void orderFactoryCreatesPendingOrderAndWithMethodsPreserveIdentity() {
        Order order = Order.create("AAPL", OrderSide.BUY, OrderType.LIMIT,
                BigDecimal.TEN, BigDecimal.valueOf(150), BigDecimal.valueOf(145),
                BigDecimal.valueOf(160), BrokerType.IBKR, "strategy-1", UUID.randomUUID());

        Order submitted = order.withExternalOrderId("ext-1").withStatus(OrderStatus.SUBMITTED);

        assertEquals(OrderStatus.PENDING, order.status());
        assertEquals(order.id(), submitted.id());
        assertEquals("ext-1", submitted.externalOrderId());
        assertEquals(OrderStatus.SUBMITTED, submitted.status());
    }

    @Test
    void positionCalculatesMarketValueAndUpdatedPnl() {
        Position position = new Position(UUID.randomUUID(), "AAPL", BigDecimal.TEN,
                BigDecimal.valueOf(100), BigDecimal.valueOf(100), BigDecimal.ZERO,
                BigDecimal.ZERO, BrokerType.IBKR, UUID.randomUUID(), Instant.now(), Instant.now());

        Position updated = position.withCurrentPrice(BigDecimal.valueOf(112));

        assertEquals(0, BigDecimal.valueOf(1120).compareTo(updated.marketValue()));
        assertEquals(0, BigDecimal.valueOf(120).compareTo(updated.unrealizedPnl()));
    }

    @Test
    void marketDataCalculatesSpreadAndMid() {
        MarketData marketData = new MarketData("AAPL", BigDecimal.valueOf(99),
                BigDecimal.valueOf(101), BigDecimal.valueOf(100), BigDecimal.TEN,
                BigDecimal.valueOf(102), BigDecimal.valueOf(98), BigDecimal.valueOf(100),
                BigDecimal.valueOf(100), Instant.now());

        assertEquals(0, BigDecimal.valueOf(2).compareTo(marketData.spread()));
        assertEquals(0, BigDecimal.valueOf(100).compareTo(marketData.mid()));
    }

    @Test
    void brokerAccountFactoryAndWithMethodsPreserveIdentity() {
        BrokerAccount account = BrokerAccount.create(UUID.randomUUID(), BrokerType.ETORO,
                "acct-1", "key", "secret", "token");

        BrokerAccount active = account.withStatus(BrokerAccountStatus.ACTIVE)
                .withCredentials("new-key", "new-secret", "new-token");

        assertEquals(BrokerAccountStatus.PENDING_VERIFICATION, account.status());
        assertEquals(account.id(), active.id());
        assertEquals(BrokerAccountStatus.ACTIVE, active.status());
        assertEquals("new-key", active.apiKey());
    }

    @Test
    void strategyConfigWithStatusChangesOnlyStatus() {
        StrategyConfig config = new StrategyConfig("s1", "RSI", "RSI", List.of("AAPL"),
                BrokerType.IBKR, StrategyStatus.PAUSED, BigDecimal.TEN, BigDecimal.ONE,
                BigDecimal.TEN, BigDecimal.valueOf(20), Map.of(), UUID.randomUUID());

        StrategyConfig active = config.withStatus(StrategyStatus.ACTIVE);

        assertEquals(StrategyStatus.ACTIVE, active.status());
        assertEquals(config.id(), active.id());
        assertEquals(config.symbols(), active.symbols());
    }
}
