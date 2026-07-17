package com.isanf.expotrade.adapters.web.controller;

import com.isanf.expotrade.application.dto.TradeResponse;
import com.isanf.expotrade.config.AuthenticatedUser;
import com.isanf.expotrade.domain.port.out.TradeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/trades")
public class TradeController {
    private final TradeRepository tradeRepository;
    public TradeController(TradeRepository tradeRepository) { this.tradeRepository = tradeRepository; }

    @GetMapping
    public ResponseEntity<List<TradeResponse>> getTrades(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = AuthenticatedUser.id(jwt);
        return ResponseEntity.ok(tradeRepository.findByUserId(userId).stream().map(TradeResponse::from).toList());
    }
}
