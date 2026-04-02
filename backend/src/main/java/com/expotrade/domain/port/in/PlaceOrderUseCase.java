package com.expotrade.domain.port.in;

import com.expotrade.domain.model.Order;
import com.expotrade.domain.model.enums.BrokerType;
import com.expotrade.domain.model.enums.OrderSide;
import com.expotrade.domain.model.enums.OrderType;
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
