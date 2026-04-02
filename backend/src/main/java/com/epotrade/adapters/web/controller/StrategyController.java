package com.epotrade.adapters.web.controller;

import com.epotrade.application.dto.StrategyRequest;
import com.epotrade.application.dto.StrategyResponse;
import com.epotrade.application.service.StrategyService;
import com.epotrade.domain.model.StrategyConfig;
import com.epotrade.domain.model.enums.StrategyStatus;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/strategies")
public class StrategyController {
    private final StrategyService strategyService;
    public StrategyController(StrategyService strategyService) { this.strategyService = strategyService; }

    @PostMapping
    public ResponseEntity<StrategyResponse> createStrategy(
            @Valid @RequestBody StrategyRequest req, @AuthenticationPrincipal UserDetails user) {
        UUID userId = UUID.fromString(user.getUsername());
        StrategyConfig config = new StrategyConfig(null, req.name(), req.type(), req.symbols(),
                req.brokerType(), StrategyStatus.PAUSED, req.maxPositionSize(),
                req.stopLossPercent(), req.takeProfitPercent(), req.maxDrawdownPercent(),
                req.parameters(), userId);
        return ResponseEntity.ok(StrategyResponse.from(strategyService.createStrategy(config)));
    }

    @PostMapping("/{id}/enable")
    public ResponseEntity<StrategyResponse> enable(@PathVariable String id, @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(StrategyResponse.from(strategyService.enableStrategy(id, UUID.fromString(user.getUsername()))));
    }

    @PostMapping("/{id}/disable")
    public ResponseEntity<StrategyResponse> disable(@PathVariable String id, @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(StrategyResponse.from(strategyService.disableStrategy(id, UUID.fromString(user.getUsername()))));
    }

    @GetMapping
    public ResponseEntity<List<StrategyResponse>> getStrategies(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(strategyService.getStrategies(UUID.fromString(user.getUsername()))
                .stream().map(StrategyResponse::from).toList());
    }
}
