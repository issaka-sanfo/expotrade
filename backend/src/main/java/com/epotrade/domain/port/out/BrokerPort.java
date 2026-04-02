package com.epotrade.domain.port.out;

import com.epotrade.domain.model.MarketData;
import com.epotrade.domain.model.Order;
import com.epotrade.domain.model.Position;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface BrokerPort {
    Mono<Order> placeOrder(Order order);
    Mono<Order> cancelOrder(String externalOrderId);
    Mono<List<Position>> getPositions();
    Mono<MarketData> getMarketData(String symbol);
    Flux<MarketData> streamMarketData(List<String> symbols);
    Mono<Order> getOrderStatus(String externalOrderId);
}
