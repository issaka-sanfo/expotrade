package com.isanf.expotrade.domain.port.in;

import com.isanf.expotrade.domain.model.Order;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface CancelOrderUseCase {
    Mono<Order> cancelOrder(UUID orderId);
}
