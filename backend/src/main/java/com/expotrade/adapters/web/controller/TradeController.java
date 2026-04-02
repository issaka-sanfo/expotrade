package com.expotrade.adapters.web.controller;

import com.expotrade.application.dto.TradeResponse;
import com.expotrade.domain.port.out.TradeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/trades")
public class TradeController {
    private final TradeRepository tradeRepository;
    public TradeController(TradeRepository tradeRepository) { this.tradeRepository = tradeRepository; }

    @GetMapping
    public ResponseEntity<List<TradeResponse>> getTrades(@AuthenticationPrincipal UserDetails user) {
        UUID userId = UUID.fromString(user.getUsername());
        return ResponseEntity.ok(tradeRepository.findByUserId(userId).stream().map(TradeResponse::from).toList());
    }
}
