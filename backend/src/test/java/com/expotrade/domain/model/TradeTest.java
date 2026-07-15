package com.expotrade.domain.model;

import com.expotrade.domain.model.enums.OrderSide;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TradeTest {

    @Test
    void totalCostIncludesPriceQuantityAndCommission() {
        Trade trade = new Trade(UUID.randomUUID(), UUID.randomUUID(), "AAPL",
                OrderSide.BUY, BigDecimal.valueOf(12), BigDecimal.valueOf(150),
                BigDecimal.valueOf(1.25), UUID.randomUUID(), Instant.now());

        assertEquals(0, BigDecimal.valueOf(1801.25).compareTo(trade.totalCost()));
    }
}
