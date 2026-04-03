package com.expotrade.adapters.web.controller;

import com.expotrade.application.dto.BrokerAccountRequest;
import com.expotrade.application.dto.BrokerAccountResponse;
import com.expotrade.application.service.BrokerAccountService;
import com.expotrade.domain.port.in.ManageBrokerAccountUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/broker-accounts")
public class BrokerAccountController {

    private final BrokerAccountService brokerAccountService;

    public BrokerAccountController(BrokerAccountService brokerAccountService) {
        this.brokerAccountService = brokerAccountService;
    }

    @PostMapping
    public ResponseEntity<BrokerAccountResponse> linkAccount(
            @Valid @RequestBody BrokerAccountRequest req,
            @AuthenticationPrincipal UserDetails user) {
        UUID userId = UUID.fromString(user.getUsername());
        var cmd = new ManageBrokerAccountUseCase.LinkBrokerAccountCommand(
                userId, req.brokerType(), req.accountId(),
                req.apiKey(), req.apiSecret(), req.accessToken());
        return ResponseEntity.ok(BrokerAccountResponse.from(brokerAccountService.linkAccount(cmd)));
    }

    @PutMapping("/{accountId}")
    public ResponseEntity<BrokerAccountResponse> updateAccount(
            @PathVariable UUID accountId,
            @Valid @RequestBody BrokerAccountRequest req,
            @AuthenticationPrincipal UserDetails user) {
        UUID userId = UUID.fromString(user.getUsername());
        var cmd = new ManageBrokerAccountUseCase.UpdateBrokerAccountCommand(
                accountId, userId, req.apiKey(), req.apiSecret(), req.accessToken());
        return ResponseEntity.ok(BrokerAccountResponse.from(brokerAccountService.updateAccount(cmd)));
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<Map<String, String>> unlinkAccount(
            @PathVariable UUID accountId,
            @AuthenticationPrincipal UserDetails user) {
        UUID userId = UUID.fromString(user.getUsername());
        brokerAccountService.unlinkAccount(accountId, userId);
        return ResponseEntity.ok(Map.of("message", "Broker account unlinked successfully"));
    }

    @GetMapping
    public ResponseEntity<List<BrokerAccountResponse>> getAccounts(@AuthenticationPrincipal UserDetails user) {
        UUID userId = UUID.fromString(user.getUsername());
        return ResponseEntity.ok(
                brokerAccountService.getAccounts(userId).stream()
                        .map(BrokerAccountResponse::from).toList());
    }

    @PostMapping("/{accountId}/verify")
    public ResponseEntity<BrokerAccountResponse> verifyAccount(
            @PathVariable UUID accountId,
            @AuthenticationPrincipal UserDetails user) {
        UUID userId = UUID.fromString(user.getUsername());
        return ResponseEntity.ok(BrokerAccountResponse.from(brokerAccountService.verifyAccount(accountId, userId)));
    }
}
