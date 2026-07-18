package com.isanf.expotrade.adapters.broker.etoro;

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

class EtoroBrokerServiceTest {

    private EtoroBrokerService brokerService;

    @BeforeEach
    void setUp() {
        brokerService = new EtoroBrokerService();
    }

    @Test
    void givenValidPaperCredentialsWhenVerifyingCredentialsThenReturnsTrue() {
        BrokerCredentials credentials = new BrokerCredentials(
                BrokerType.ETORO, "paper-account", "api-key", "api-secret", "access-token");

        Boolean verified = brokerService.verifyCredentials(credentials).block(Duration.ofSeconds(1));

        assertThat(verified).isTrue();
    }

    @Test
    void givenBlankPaperCredentialsWhenVerifyingCredentialsThenReturnsFalse() {
        BrokerCredentials credentials = new BrokerCredentials(
                BrokerType.ETORO, "paper-account", "api-key", "", "access-token");

        Boolean verified = brokerService.verifyCredentials(credentials).block(Duration.ofSeconds(1));

        assertThat(verified).isFalse();
    }

    @Test
    void givenPaperOrderWhenPlacingOrderThenReturnsSubmittedEtoroOrder() {
        Order order = paperOrder(BrokerType.ETORO, "TSLA");

        Order placed = brokerService.placeOrder(order).block(Duration.ofSeconds(1));

        assertThat(placed).isNotNull();
        assertThat(placed.status()).isEqualTo(OrderStatus.SUBMITTED);
        assertThat(placed.externalOrderId()).startsWith("ETORO-");
        assertThat(placed.symbol()).isEqualTo("TSLA");
        assertThat(placed.brokerType()).isEqualTo(BrokerType.ETORO);
    }

    @Test
    void givenEtoroPaperAdapterWhenGettingPositionsThenReturnsSimulatedEtoroPositions() {
        List<Position> positions = brokerService.getPositions().block(Duration.ofSeconds(1));

        assertThat(positions).isNotNull();
        assertThat(positions).hasSize(1);
        assertThat(positions)
                .extracting(Position::brokerType)
                .containsOnly(BrokerType.ETORO);
    }

    @Test
    void givenKnownSymbolWhenGettingMarketDataThenReturnsSimulatedQuote() {
        MarketData marketData = brokerService.getMarketData("TSLA").block(Duration.ofSeconds(1));

        assertThat(marketData).isNotNull();
        assertThat(marketData.symbol()).isEqualTo("TSLA");
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
