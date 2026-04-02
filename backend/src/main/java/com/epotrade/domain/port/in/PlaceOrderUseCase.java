package com.epotrade.domain.port.in;

import com.epotrade.domain.model.Order;
import com.epotrade.domain.model.enums.BrokerType;
import com.epotrade.domain.model.enums.OrderSide;
import com.epotrade.domain.model.enums.OrderType;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

public interface PlaceOrderUseCase {

    record PlaceOrderCommand(
            String symbol,
            OrderSide side,
            OrderType type,
            BigDecimal quantity,
            BigDecimal price,
            BigDecimal stopLoss,
            BigDecimal takeProfit,
            BrokerType brokerType,
            String strategyId,
            UUID userId
    ) {}

    Mono<Order> placeOrder(PlaceOrderCommand command);
}
