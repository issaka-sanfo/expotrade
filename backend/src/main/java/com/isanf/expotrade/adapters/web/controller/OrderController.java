package com.isanf.expotrade.adapters.web.controller;

import com.isanf.expotrade.application.dto.OrderRequest;
import com.isanf.expotrade.application.dto.OrderResponse;
import com.isanf.expotrade.application.service.OrderService;
import com.isanf.expotrade.config.AuthenticatedUser;
import com.isanf.expotrade.domain.port.in.PlaceOrderUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Orders", description = "Order placement, cancellation and user order queries")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) { this.orderService = orderService; }

    @PostMapping
    @Operation(summary = "Place an order for the authenticated user")
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
    @Operation(summary = "Cancel an order owned by the authenticated user")
    public Mono<ResponseEntity<OrderResponse>> cancelOrder(
            @PathVariable UUID orderId,
            @AuthenticationPrincipal Jwt jwt) {
        UUID userId = AuthenticatedUser.id(jwt);
        return orderService.cancelOrder(orderId, userId).map(o -> ResponseEntity.ok(OrderResponse.from(o)));
    }

    @GetMapping
    @Operation(summary = "List orders owned by the authenticated user")
    public ResponseEntity<List<OrderResponse>> getOrders(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = AuthenticatedUser.id(jwt);
        return ResponseEntity.ok(orderService.getOrdersByUser(userId).stream().map(OrderResponse::from).toList());
    }

    @GetMapping("/strategy/{strategyId}")
    @Operation(summary = "List authenticated user orders for a strategy")
    public ResponseEntity<List<OrderResponse>> getOrdersByStrategy(
            @PathVariable String strategyId,
            @AuthenticationPrincipal Jwt jwt) {
        UUID userId = AuthenticatedUser.id(jwt);
        return ResponseEntity.ok(orderService.getOrdersByUserAndStrategy(userId, strategyId).stream().map(OrderResponse::from).toList());
    }
}
