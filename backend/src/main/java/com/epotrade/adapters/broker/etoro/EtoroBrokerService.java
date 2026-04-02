package com.epotrade.adapters.broker.etoro;

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
 * eToro API adapter (simulated wrapper).
 * eToro does not provide a public trading API — this is a mock implementation.
 */
@Component("ETORO")
public class EtoroBrokerService implements BrokerPort {

    private static final Logger log = LoggerFactory.getLogger(EtoroBrokerService.class);
    private final AtomicLong orderCounter = new AtomicLong(1);

    @Override
    public Mono<Order> placeOrder(Order order) {
        return Mono.fromCallable(() -> {
            log.info("[eToro] Placing order: {} {} {} @ {}",
                    order.side(), order.quantity(), order.symbol(), order.price());
            String externalId = "ETORO-" + orderCounter.getAndIncrement();
            return order.withExternalOrderId(externalId).withStatus(OrderStatus.SUBMITTED);
        }).delayElement(Duration.ofMillis(200));
    }

    @Override
    public Mono<Order> cancelOrder(String externalOrderId) {
        log.info("[eToro] Cancelling order: {}", externalOrderId);
        return Mono.empty();
    }

    @Override
    public Mono<List<Position>> getPositions() {
        return Mono.fromCallable(() -> List.of(
                new Position(UUID.randomUUID(), "TSLA", BigDecimal.valueOf(25),
                        BigDecimal.valueOf(240.0), BigDecimal.valueOf(245.0),
                        BigDecimal.valueOf(125), BigDecimal.ZERO, BrokerType.ETORO,
                        UUID.randomUUID(), Instant.now().minusSeconds(86400), Instant.now())
        ));
    }

    @Override
    public Mono<MarketData> getMarketData(String symbol) {
        return Mono.fromCallable(() -> {
            double basePrice = switch (symbol) {
                case "TSLA" -> 245.0; case "NVDA" -> 880.0; case "META" -> 500.0;
                default -> 100.0;
            };
            double variation = (Math.random() - 0.5) * 2;
            BigDecimal price = BigDecimal.valueOf(basePrice + variation);
            return new MarketData(symbol,
                    price.subtract(BigDecimal.valueOf(0.10)), price.add(BigDecimal.valueOf(0.10)),
                    price, BigDecimal.valueOf(Math.random() * 5_000_000),
                    price.add(BigDecimal.valueOf(3)), price.subtract(BigDecimal.valueOf(3)),
                    price.subtract(BigDecimal.valueOf(1.5)), price, Instant.now());
        });
    }

    @Override
    public Flux<MarketData> streamMarketData(List<String> symbols) {
        return Flux.interval(Duration.ofSeconds(2))
                .flatMap(tick -> Flux.fromIterable(symbols).flatMap(this::getMarketData));
    }

    @Override
    public Mono<Order> getOrderStatus(String externalOrderId) {
        log.info("[eToro] Getting order status: {}", externalOrderId);
        return Mono.empty();
    }
}
