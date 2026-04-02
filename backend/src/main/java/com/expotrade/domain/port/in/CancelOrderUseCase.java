package com.expotrade.domain.port.in;

import com.expotrade.domain.model.Order;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface CancelOrderUseCase {
    Mono<Order> cancelOrder(UUID orderId);
}
