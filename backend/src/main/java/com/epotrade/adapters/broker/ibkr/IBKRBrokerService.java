package com.epotrade.adapters.broker.ibkr;

import com.epotrade.domain.model.MarketData;
import com.epotrade.domain.model.Order;
import com.epotrade.domain.model.Position;
import com.epotrade.domain.model.enums.BrokerType;
import com.epotrade.domain.model.enums.OrderStatus;
import com.epotrade.domain.port.out.BrokerPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Interactive Brokers API adapter.
 * Simulated implementation for development — in production would integrate with TWS/IB Gateway.
 */
@Component("IBKR")
public class IBKRBrokerService implements BrokerPort {

    private static final Logger log = LoggerFactory.getLogger(IBKRBrokerService.class);
    private final AtomicLong orderCounter = new AtomicLong(1);

    @Override
    public Mono<Order> placeOrder(Order order) {
        return Mono.fromCallable(() -> {
            log.info("[IBKR] Placing order: {} {} {} @ {}",
                    order.side(), order.quantity(), order.symbol(), order.price());
            String externalId = "IBKR-" + orderCounter.getAndIncrement();
            return order.withExternalOrderId(externalId).withStatus(OrderStatus.SUBMITTED);
        }).delayElement(Duration.ofMillis(100));
    }

    @Override
    public Mono<Order> cancelOrder(String externalOrderId) {
        return Mono.fromCallable(() -> {
            log.info("[IBKR] Cancelling order: {}", externalOrderId);
            return (Order) null;
        }).delayElement(Duration.ofMillis(50));
    }

    @Override
    public Mono<List<Position>> getPositions() {
        return Mono.fromCallable(() -> List.of(
                new Position(UUID.randomUUID(), "AAPL", BigDecimal.valueOf(100),
                        BigDecimal.valueOf(150.0), BigDecimal.valueOf(155.0),
                        BigDecimal.valueOf(500), BigDecimal.ZERO, BrokerType.IBKR,
                        UUID.randomUUID(), Instant.now().minusSeconds(86400), Instant.now()),
                new Position(UUID.randomUUID(), "MSFT", BigDecimal.valueOf(50),
                        BigDecimal.valueOf(380.0), BigDecimal.valueOf(385.0),
                        BigDecimal.valueOf(250), BigDecimal.ZERO, BrokerType.IBKR,
                        UUID.randomUUID(), Instant.now().minusSeconds(172800), Instant.now())
        ));
    }

    @Override
    public Mono<MarketData> getMarketData(String symbol) {
        return Mono.fromCallable(() -> {
            double basePrice = switch (symbol) {
                case "AAPL" -> 155.0; case "MSFT" -> 385.0;
                case "GOOGL" -> 140.0; case "AMZN" -> 178.0;
                default -> 100.0;
            };
            double variation = (Math.random() - 0.5) * 2;
            BigDecimal price = BigDecimal.valueOf(basePrice + variation);
            return new MarketData(symbol,
                    price.subtract(BigDecimal.valueOf(0.05)), price.add(BigDecimal.valueOf(0.05)),
                    price, BigDecimal.valueOf(Math.random() * 10_000_000),
                    price.add(BigDecimal.valueOf(2)), price.subtract(BigDecimal.valueOf(2)),
                    price.subtract(BigDecimal.valueOf(1)), price, Instant.now());
        });
    }

    @Override
    public Flux<MarketData> streamMarketData(List<String> symbols) {
        return Flux.interval(Duration.ofSeconds(1))
                .flatMap(tick -> Flux.fromIterable(symbols).flatMap(this::getMarketData));
    }

    @Override
    public Mono<Order> getOrderStatus(String externalOrderId) {
        log.info("[IBKR] Getting order status: {}", externalOrderId);
        return Mono.empty();
    }
}
