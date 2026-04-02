package com.expotrade.application.service;

import com.expotrade.domain.model.Order;
import com.expotrade.domain.model.enums.OrderStatus;
import com.expotrade.domain.port.in.CancelOrderUseCase;
import com.expotrade.domain.port.in.PlaceOrderUseCase;
import com.expotrade.domain.port.out.BrokerPort;
import com.expotrade.domain.port.out.EventPublisher;
import com.expotrade.domain.port.out.OrderRepository;
import com.expotrade.domain.service.RiskManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class OrderService implements PlaceOrderUseCase, CancelOrderUseCase {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final Map<String, BrokerPort> brokerPorts;
    private final OrderRepository orderRepository;
    private final EventPublisher eventPublisher;
    private final RiskManager riskManager;

    public OrderService(Map<String, BrokerPort> brokerPorts,
                        OrderRepository orderRepository,
                        EventPublisher eventPublisher,
                        RiskManager riskManager) {
        this.brokerPorts = brokerPorts;
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
        this.riskManager = riskManager;
    }

    @Override
    public Mono<Order> placeOrder(PlaceOrderCommand command) {
        Order order = Order.create(
                command.symbol(), command.side(), command.type(),
                command.quantity(), command.price(),
                command.stopLoss(), command.takeProfit(),
                command.brokerType(), command.strategyId(), command.userId()
        );

        Order savedOrder = orderRepository.save(order);
        log.info("Order created: {} for {} {}", savedOrder.id(), savedOrder.symbol(), savedOrder.side());

        BrokerPort broker = brokerPorts.get(command.brokerType().name());
        if (broker == null) {
            return Mono.error(new IllegalArgumentException("Unsupported broker: " + command.brokerType()));
        }

        return broker.placeOrder(savedOrder)
                .map(executedOrder -> {
                    Order updated = orderRepository.save(executedOrder);
                    eventPublisher.publishOrderEvent("ORDER_PLACED", updated);
                    log.info("Order submitted to broker: {}", updated.externalOrderId());
                    return updated;
                })
                .onErrorResume(e -> {
                    log.error("Failed to place order: {}", e.getMessage());
                    Order rejected = savedOrder.withStatus(OrderStatus.REJECTED);
                    orderRepository.save(rejected);
                    eventPublisher.publishOrderEvent("ORDER_REJECTED", rejected);
                    return Mono.error(e);
                });
    }

    @Override
    public Mono<Order> cancelOrder(UUID orderId) {
        return Mono.justOrEmpty(orderRepository.findById(orderId))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Order not found: " + orderId)))
                .flatMap(order -> {
                    BrokerPort broker = brokerPorts.get(order.brokerType().name());
                    if (broker == null) {
                        return Mono.error(new IllegalArgumentException("Unsupported broker"));
                    }
                    return broker.cancelOrder(order.externalOrderId())
                            .map(cancelledOrder -> {
                                Order updated = order.withStatus(OrderStatus.CANCELLED);
                                orderRepository.save(updated);
                                eventPublisher.publishOrderEvent("ORDER_CANCELLED", updated);
                                return updated;
                            });
                });
    }

    public List<Order> getOrdersByUser(UUID userId) {
        return orderRepository.findByUserId(userId);
    }

    public List<Order> getOrdersByStrategy(String strategyId) {
        return orderRepository.findByStrategyId(strategyId);
    }
}
