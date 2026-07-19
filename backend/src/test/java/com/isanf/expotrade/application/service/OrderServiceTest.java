package com.isanf.expotrade.application.service;

import com.isanf.expotrade.domain.model.Order;
import com.isanf.expotrade.domain.model.Portfolio;
import com.isanf.expotrade.domain.model.StrategyConfig;
import com.isanf.expotrade.domain.model.enums.BrokerType;
import com.isanf.expotrade.domain.model.enums.OrderSide;
import com.isanf.expotrade.domain.model.enums.OrderStatus;
import com.isanf.expotrade.domain.model.enums.OrderType;
import com.isanf.expotrade.domain.model.enums.StrategyStatus;
import com.isanf.expotrade.domain.port.in.PlaceOrderUseCase.PlaceOrderCommand;
import com.isanf.expotrade.domain.port.out.BrokerPort;
import com.isanf.expotrade.domain.port.out.EventPublisher;
import com.isanf.expotrade.domain.port.out.OrderRepository;
import com.isanf.expotrade.domain.service.RiskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    private BrokerPort broker;
    private OrderRepository orderRepository;
    private EventPublisher eventPublisher;
    private StrategyService strategyService;
    private PortfolioService portfolioService;
    private OrderService service;

    @BeforeEach
    void setUp() {
        broker = mock(BrokerPort.class);
        orderRepository = mock(OrderRepository.class);
        eventPublisher = mock(EventPublisher.class);
        strategyService = mock(StrategyService.class);
        portfolioService = mock(PortfolioService.class);
        service = new OrderService(Map.of(BrokerType.IBKR.name(), broker), orderRepository,
                eventPublisher, new RiskManager(), strategyService, portfolioService);
    }

    @Test
    void placeOrderPersistsBrokerResultAndPublishesEvent() {
        PlaceOrderCommand command = command();
        givenRiskContext(command.strategyId(), command.userId(), BigDecimal.valueOf(5000), BigDecimal.valueOf(10),
                BigDecimal.valueOf(100000), BigDecimal.valueOf(50000), BigDecimal.ZERO);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(broker.placeOrder(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            return Mono.just(order.withExternalOrderId("ext-1").withStatus(OrderStatus.SUBMITTED));
        });

        Order placed = service.placeOrder(command).block();

        assertNotNull(placed);
        assertEquals(OrderStatus.SUBMITTED, placed.status());
        assertEquals("ext-1", placed.externalOrderId());
        verify(orderRepository, times(2)).save(any(Order.class));
        verify(eventPublisher).publishOrderEvent(eq("ORDER_PLACED"), same(placed));
    }

    @Test
    void placeOrderRejectsSavedOrderWhenBrokerFails() {
        PlaceOrderCommand command = command();
        givenRiskContext(command.strategyId(), command.userId(), BigDecimal.valueOf(5000), BigDecimal.valueOf(10),
                BigDecimal.valueOf(100000), BigDecimal.valueOf(50000), BigDecimal.ZERO);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(broker.placeOrder(any(Order.class))).thenReturn(Mono.error(new IllegalStateException("broker down")));

        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> service.placeOrder(command).block());

        assertEquals("broker down", error.getMessage());
        verify(eventPublisher).publishOrderEvent(eq("ORDER_REJECTED"),
                argThat(payload -> ((Order) payload).status() == OrderStatus.REJECTED));
    }

    @Test
    void placeOrderRejectsBeforeBrokerWhenRiskCheckFails() {
        PlaceOrderCommand command = command();
        givenRiskContext(command.strategyId(), command.userId(), BigDecimal.valueOf(1000), BigDecimal.valueOf(10),
                BigDecimal.valueOf(100000), BigDecimal.valueOf(50000), BigDecimal.ZERO);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PreTradeRiskRejectedException error = assertThrows(PreTradeRiskRejectedException.class,
                () -> service.placeOrder(command).block());

        assertEquals("MAX_POSITION_SIZE_EXCEEDED", error.code());
        verify(broker, never()).placeOrder(any(Order.class));
        verify(eventPublisher).publishOrderEvent(eq("ORDER_REJECTED"), any(Order.class));
        verify(orderRepository).save(argThat(order -> order.status() == OrderStatus.REJECTED));
    }

    @Test
    void cancelOrderDelegatesToBrokerAndMarksCancelled() {
        Order order = sampleOrder().withExternalOrderId("ext-1").withStatus(OrderStatus.SUBMITTED);
        when(orderRepository.findById(order.id())).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(broker.cancelOrder("ext-1")).thenReturn(Mono.just(order));

        Order cancelled = service.cancelOrder(order.id(), order.userId()).block();

        assertNotNull(cancelled);
        assertEquals(OrderStatus.CANCELLED, cancelled.status());
        verify(eventPublisher).publishOrderEvent("ORDER_CANCELLED", cancelled);
    }

    @Test
    void cancelOrderRejectsOrderOwnedByAnotherUser() {
        Order order = sampleOrder().withExternalOrderId("ext-1").withStatus(OrderStatus.SUBMITTED);
        when(orderRepository.findById(order.id())).thenReturn(Optional.of(order));

        SecurityException error = assertThrows(SecurityException.class,
                () -> service.cancelOrder(order.id(), UUID.randomUUID()).block());

        assertEquals("Order does not belong to current user", error.getMessage());
        verify(broker, never()).cancelOrder(anyString());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void getOrdersByUserReturnsRepositoryResults() {
        UUID userId = UUID.randomUUID();
        List<Order> orders = List.of(sampleOrder());
        when(orderRepository.findByUserId(userId)).thenReturn(orders);

        assertSame(orders, service.getOrdersByUser(userId));
    }

    @Test
    void getOrdersByUserAndStrategyReturnsRepositoryResults() {
        UUID userId = UUID.randomUUID();
        List<Order> orders = List.of(sampleOrder());
        when(orderRepository.findByUserIdAndStrategyId(userId, "strategy-1")).thenReturn(orders);

        assertSame(orders, service.getOrdersByUserAndStrategy(userId, "strategy-1"));
    }

    private PlaceOrderCommand command() {
        UUID userId = UUID.randomUUID();
        return new PlaceOrderCommand("AAPL", OrderSide.BUY, OrderType.MARKET,
                BigDecimal.TEN, BigDecimal.valueOf(150), null, null,
                BrokerType.IBKR, "strategy-1", userId);
    }

    private Order sampleOrder() {
        return Order.create("AAPL", OrderSide.BUY, OrderType.MARKET,
                BigDecimal.TEN, BigDecimal.valueOf(150), null, null,
                BrokerType.IBKR, "strategy-1", UUID.randomUUID());
    }

    private void givenRiskContext(String strategyId, UUID userId, BigDecimal maxPositionSize,
                                  BigDecimal maxDrawdownPercent, BigDecimal totalValue,
                                  BigDecimal cashBalance, BigDecimal maxDrawdown) {
        when(strategyService.getStrategy(strategyId)).thenReturn(new StrategyConfig(
                strategyId, "Test", "RSI", List.of("AAPL"), BrokerType.IBKR,
                StrategyStatus.ACTIVE, maxPositionSize, BigDecimal.valueOf(2),
                BigDecimal.valueOf(5), maxDrawdownPercent, Map.of(), userId
        ));
        when(portfolioService.getPortfolio(userId)).thenReturn(Mono.just(new Portfolio(
                userId, totalValue, cashBalance, BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.ZERO, maxDrawdown, List.of()
        )));
    }
}
