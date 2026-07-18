package com.isanf.expotrade.adapters.broker.ibkr;

import com.isanf.expotrade.domain.model.BrokerCredentials;
import com.isanf.expotrade.domain.model.MarketData;
import com.isanf.expotrade.domain.model.Order;
import com.isanf.expotrade.domain.model.Position;
import com.isanf.expotrade.domain.model.enums.BrokerType;
import com.isanf.expotrade.domain.model.enums.OrderSide;
import com.isanf.expotrade.domain.model.enums.OrderStatus;
import com.isanf.expotrade.domain.model.enums.OrderType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class IBKRBrokerServiceTest {

    private IBKRBrokerService brokerService;

    @BeforeEach
    void setUp() {
        brokerService = new IBKRBrokerService();
    }

    @Test
    void givenValidPaperCredentialsWhenVerifyingCredentialsThenReturnsTrue() {
        BrokerCredentials credentials = new BrokerCredentials(
                BrokerType.IBKR, "paper-account", "api-key", "api-secret", "access-token");

        Boolean verified = brokerService.verifyCredentials(credentials).block(Duration.ofSeconds(1));

        assertThat(verified).isTrue();
    }

    @Test
    void givenBlankPaperCredentialsWhenVerifyingCredentialsThenReturnsFalse() {
        BrokerCredentials credentials = new BrokerCredentials(
                BrokerType.IBKR, "paper-account", "", "api-secret", "access-token");

        Boolean verified = brokerService.verifyCredentials(credentials).block(Duration.ofSeconds(1));

        assertThat(verified).isFalse();
    }

    @Test
    void givenPaperOrderWhenPlacingOrderThenReturnsSubmittedIbkrOrder() {
        Order order = paperOrder(BrokerType.IBKR, "AAPL");

        Order placed = brokerService.placeOrder(order).block(Duration.ofSeconds(1));

        assertThat(placed).isNotNull();
        assertThat(placed.status()).isEqualTo(OrderStatus.SUBMITTED);
        assertThat(placed.externalOrderId()).startsWith("IBKR-");
        assertThat(placed.symbol()).isEqualTo("AAPL");
        assertThat(placed.brokerType()).isEqualTo(BrokerType.IBKR);
    }

    @Test
    void givenExternalOrderIdWhenCancellingOrderThenReturnsCancelledIbkrPaperOrder() {
        Order cancelled = brokerService.cancelOrder("IBKR-1").block(Duration.ofSeconds(1));

        assertThat(cancelled).isNotNull();
        assertThat(cancelled.status()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(cancelled.externalOrderId()).isEqualTo("IBKR-1");
        assertThat(cancelled.brokerType()).isEqualTo(BrokerType.IBKR);
    }

    @Test
    void givenIbkrPaperAdapterWhenGettingPositionsThenReturnsSimulatedIbkrPositions() {
        List<Position> positions = brokerService.getPositions().block(Duration.ofSeconds(1));

        assertThat(positions).isNotNull();
        assertThat(positions).hasSize(2);
        assertThat(positions)
                .extracting(Position::brokerType)
                .containsOnly(BrokerType.IBKR);
    }

    @Test
    void givenKnownSymbolWhenGettingMarketDataThenReturnsSimulatedQuote() {
        MarketData marketData = brokerService.getMarketData("AAPL").block(Duration.ofSeconds(1));

        assertThat(marketData).isNotNull();
        assertThat(marketData.symbol()).isEqualTo("AAPL");
        assertThat(marketData.bid()).isLessThan(marketData.ask());
        assertThat(marketData.last()).isPositive();
        assertThat(marketData.volume()).isNotNegative();
    }

    private Order paperOrder(BrokerType brokerType, String symbol) {
        return Order.create(symbol, OrderSide.BUY, OrderType.MARKET,
                BigDecimal.TEN, BigDecimal.valueOf(150), null, null,
                brokerType, "strategy-1", UUID.randomUUID());
    }
}
