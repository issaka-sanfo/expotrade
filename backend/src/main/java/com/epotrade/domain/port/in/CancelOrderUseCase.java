package com.epotrade.domain.port.in;

import com.epotrade.domain.model.Order;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface CancelOrderUseCase {
    Mono<Order> cancelOrder(UUID orderId);
}
