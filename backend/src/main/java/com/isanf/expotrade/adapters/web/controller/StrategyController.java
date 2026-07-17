package com.isanf.expotrade.adapters.web.controller;

import com.isanf.expotrade.application.dto.StrategyRequest;
import com.isanf.expotrade.application.dto.StrategyResponse;
import com.isanf.expotrade.application.service.StrategyService;
import com.isanf.expotrade.config.AuthenticatedUser;
import com.isanf.expotrade.domain.model.StrategyConfig;
import com.isanf.expotrade.domain.model.enums.StrategyStatus;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
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
            @Valid @RequestBody StrategyRequest req, @AuthenticationPrincipal Jwt jwt) {
        UUID userId = AuthenticatedUser.id(jwt);
        StrategyConfig config = new StrategyConfig(null, req.name(), req.type(), req.symbols(),
                req.brokerType(), StrategyStatus.PAUSED, req.maxPositionSize(),
                req.stopLossPercent(), req.takeProfitPercent(), req.maxDrawdownPercent(),
                req.parameters(), userId);
        return ResponseEntity.ok(StrategyResponse.from(strategyService.createStrategy(config)));
    }

    @PostMapping("/{id}/enable")
    public ResponseEntity<StrategyResponse> enable(@PathVariable String id, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(StrategyResponse.from(strategyService.enableStrategy(id, AuthenticatedUser.id(jwt))));
    }

    @PostMapping("/{id}/disable")
    public ResponseEntity<StrategyResponse> disable(@PathVariable String id, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(StrategyResponse.from(strategyService.disableStrategy(id, AuthenticatedUser.id(jwt))));
    }

    @GetMapping
    public ResponseEntity<List<StrategyResponse>> getStrategies(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(strategyService.getStrategies(AuthenticatedUser.id(jwt))
                .stream().map(StrategyResponse::from).toList());
    }
}
