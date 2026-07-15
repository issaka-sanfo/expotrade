package com.expotrade.application.service;

import com.expotrade.domain.model.Order;
import com.expotrade.domain.model.enums.BrokerType;
import com.expotrade.domain.model.enums.OrderSide;
import com.expotrade.domain.model.enums.OrderStatus;
import com.expotrade.domain.model.enums.OrderType;
import com.expotrade.domain.port.in.PlaceOrderUseCase.PlaceOrderCommand;
import com.expotrade.domain.port.out.BrokerPort;
import com.expotrade.domain.port.out.EventPublisher;
import com.expotrade.domain.port.out.OrderRepository;
import com.expotrade.domain.service.RiskManager;
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
    private OrderService service;

    @BeforeEach
    void setUp() {
        broker = mock(BrokerPort.class);
        orderRepository = mock(OrderRepository.class);
        eventPublisher = mock(EventPublisher.class);
        service = new OrderService(Map.of(BrokerType.IBKR.name(), broker), orderRepository,
                eventPublisher, new RiskManager());
    }

    @Test
    void placeOrderPersistsBrokerResultAndPublishesEvent() {
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(broker.placeOrder(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            return Mono.just(order.withExternalOrderId("ext-1").withStatus(OrderStatus.SUBMITTED));
        });

        Order placed = service.placeOrder(command()).block();

        assertNotNull(placed);
        assertEquals(OrderStatus.SUBMITTED, placed.status());
        assertEquals("ext-1", placed.externalOrderId());
        verify(orderRepository, times(2)).save(any(Order.class));
        verify(eventPublisher).publishOrderEvent(eq("ORDER_PLACED"), same(placed));
    }

    @Test
    void placeOrderRejectsSavedOrderWhenBrokerFails() {
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(broker.placeOrder(any(Order.class))).thenReturn(Mono.error(new IllegalStateException("broker down")));

        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> service.placeOrder(command()).block());

        assertEquals("broker down", error.getMessage());
        verify(eventPublisher).publishOrderEvent(eq("ORDER_REJECTED"),
                argThat(payload -> ((Order) payload).status() == OrderStatus.REJECTED));
    }

    @Test
    void cancelOrderDelegatesToBrokerAndMarksCancelled() {
        Order order = sampleOrder().withExternalOrderId("ext-1").withStatus(OrderStatus.SUBMITTED);
        when(orderRepository.findById(order.id())).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(broker.cancelOrder("ext-1")).thenReturn(Mono.just(order));

        Order cancelled = service.cancelOrder(order.id()).block();

        assertNotNull(cancelled);
        assertEquals(OrderStatus.CANCELLED, cancelled.status());
        verify(eventPublisher).publishOrderEvent("ORDER_CANCELLED", cancelled);
    }

    @Test
    void getOrdersByUserReturnsRepositoryResults() {
        UUID userId = UUID.randomUUID();
        List<Order> orders = List.of(sampleOrder());
        when(orderRepository.findByUserId(userId)).thenReturn(orders);

        assertSame(orders, service.getOrdersByUser(userId));
    }

    private PlaceOrderCommand command() {
        return new PlaceOrderCommand("AAPL", OrderSide.BUY, OrderType.MARKET,
                BigDecimal.TEN, BigDecimal.valueOf(150), null, null,
                BrokerType.IBKR, "strategy-1", UUID.randomUUID());
    }

    private Order sampleOrder() {
        return Order.create("AAPL", OrderSide.BUY, OrderType.MARKET,
                BigDecimal.TEN, BigDecimal.valueOf(150), null, null,
                BrokerType.IBKR, "strategy-1", UUID.randomUUID());
    }
}
