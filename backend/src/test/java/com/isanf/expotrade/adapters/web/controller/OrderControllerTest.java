package com.isanf.expotrade.adapters.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isanf.expotrade.application.dto.OrderRequest;
import com.isanf.expotrade.application.service.OrderService;
import com.isanf.expotrade.config.SecurityConfig;
import com.isanf.expotrade.domain.model.Order;
import com.isanf.expotrade.domain.model.enums.BrokerType;
import com.isanf.expotrade.domain.model.enums.OrderSide;
import com.isanf.expotrade.domain.model.enums.OrderStatus;
import com.isanf.expotrade.domain.model.enums.OrderType;
import com.isanf.expotrade.domain.port.in.PlaceOrderUseCase.PlaceOrderCommand;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = OrderController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class OrderControllerTest {

    private static final UUID USER_ID = UUID.fromString("8ac0d1dd-83c8-4e78-8fb1-eab109b165d3");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    void postOrderWithValidJwtPlacesOrder() throws Exception {
        Order order = sampleOrder(USER_ID).withExternalOrderId("ext-1").withStatus(OrderStatus.SUBMITTED);
        OrderRequest request = new OrderRequest("AAPL", OrderSide.BUY, OrderType.MARKET,
                BigDecimal.TEN, BigDecimal.valueOf(150), null, null, BrokerType.IBKR, "strategy-1");
        when(orderService.placeOrder(any(PlaceOrderCommand.class))).thenReturn(Mono.just(order));

        MvcResult result = mockMvc.perform(post("/api/v1/orders")
                        .with(jwt().jwt(jwt -> jwt.subject(USER_ID.toString())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.symbol").value("AAPL"))
                .andExpect(jsonPath("$.status").value("SUBMITTED"))
                .andExpect(jsonPath("$.externalOrderId").value("ext-1"));

        verify(orderService).placeOrder(new PlaceOrderCommand("AAPL", OrderSide.BUY, OrderType.MARKET,
                BigDecimal.TEN, BigDecimal.valueOf(150), null, null, BrokerType.IBKR, "strategy-1", USER_ID));
    }

    @Test
    void postOrderWithInvalidPayloadReturnsBadRequest() throws Exception {
        String invalidPayload = """
                {
                  "symbol": "",
                  "side": "BUY",
                  "type": "MARKET",
                  "quantity": 10,
                  "brokerType": "IBKR",
                  "strategyId": "strategy-1"
                }
                """;

        mockMvc.perform(post("/api/v1/orders")
                        .with(jwt().jwt(jwt -> jwt.subject(USER_ID.toString())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getOrdersUsesAuthenticatedUser() throws Exception {
        when(orderService.getOrdersByUser(USER_ID)).thenReturn(List.of(sampleOrder(USER_ID)));

        mockMvc.perform(get("/api/v1/orders")
                        .with(jwt().jwt(jwt -> jwt.subject(USER_ID.toString()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].symbol").value("AAPL"));

        verify(orderService).getOrdersByUser(USER_ID);
    }

    @Test
    void getOrdersByStrategyUsesAuthenticatedUserAndStrategy() throws Exception {
        when(orderService.getOrdersByUserAndStrategy(USER_ID, "strategy-1")).thenReturn(List.of(sampleOrder(USER_ID)));

        mockMvc.perform(get("/api/v1/orders/strategy/strategy-1")
                        .with(jwt().jwt(jwt -> jwt.subject(USER_ID.toString()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].symbol").value("AAPL"));

        verify(orderService).getOrdersByUserAndStrategy(USER_ID, "strategy-1");
    }

    @Test
    void deleteOrderUsesAuthenticatedUser() throws Exception {
        UUID orderId = UUID.randomUUID();
        Order order = sampleOrder(USER_ID).withExternalOrderId("ext-1").withStatus(OrderStatus.CANCELLED);
        when(orderService.cancelOrder(orderId, USER_ID)).thenReturn(Mono.just(order));

        MvcResult result = mockMvc.perform(delete("/api/v1/orders/{orderId}", orderId)
                        .with(jwt().jwt(jwt -> jwt.subject(USER_ID.toString()))))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        verify(orderService).cancelOrder(orderId, USER_ID);
    }

    @Test
    void ordersEndpointRejectsAnonymousRequest() throws Exception {
        mockMvc.perform(get("/api/v1/orders"))
                .andExpect(status().isUnauthorized());
    }

    private Order sampleOrder(UUID userId) {
        return Order.create("AAPL", OrderSide.BUY, OrderType.MARKET,
                BigDecimal.TEN, BigDecimal.valueOf(150), null, null,
                BrokerType.IBKR, "strategy-1", userId);
    }
}
