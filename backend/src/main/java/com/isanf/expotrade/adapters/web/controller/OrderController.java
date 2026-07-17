package com.isanf.expotrade.adapters.web.controller;

import com.isanf.expotrade.application.dto.OrderRequest;
import com.isanf.expotrade.application.dto.OrderResponse;
import com.isanf.expotrade.application.service.OrderService;
import com.isanf.expotrade.config.AuthenticatedUser;
import com.isanf.expotrade.domain.port.in.PlaceOrderUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) { this.orderService = orderService; }

    @PostMapping
    public Mono<ResponseEntity<OrderResponse>> placeOrder(
            @Valid @RequestBody OrderRequest req,
            @AuthenticationPrincipal Jwt jwt) {
        UUID userId = AuthenticatedUser.id(jwt);
        var cmd = new PlaceOrderUseCase.PlaceOrderCommand(
                req.symbol(), req.side(), req.type(), req.quantity(), req.price(),
                req.stopLoss(), req.takeProfit(), req.brokerType(), req.strategyId(), userId);
        return orderService.placeOrder(cmd).map(o -> ResponseEntity.ok(OrderResponse.from(o)));
    }

    @DeleteMapping("/{orderId}")
    public Mono<ResponseEntity<OrderResponse>> cancelOrder(@PathVariable UUID orderId) {
        return orderService.cancelOrder(orderId).map(o -> ResponseEntity.ok(OrderResponse.from(o)));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrders(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = AuthenticatedUser.id(jwt);
        return ResponseEntity.ok(orderService.getOrdersByUser(userId).stream().map(OrderResponse::from).toList());
    }

    @GetMapping("/strategy/{strategyId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByStrategy(@PathVariable String strategyId) {
        return ResponseEntity.ok(orderService.getOrdersByStrategy(strategyId).stream().map(OrderResponse::from).toList());
    }
}
