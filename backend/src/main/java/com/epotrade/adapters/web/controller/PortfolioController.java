package com.epotrade.adapters.web.controller;

import com.epotrade.application.dto.PortfolioResponse;
import com.epotrade.application.service.PortfolioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/portfolio")
public class PortfolioController {
    private final PortfolioService portfolioService;
    public PortfolioController(PortfolioService portfolioService) { this.portfolioService = portfolioService; }

    @GetMapping
    public Mono<ResponseEntity<PortfolioResponse>> getPortfolio(@AuthenticationPrincipal UserDetails user) {
        UUID userId = UUID.fromString(user.getUsername());
        return portfolioService.getPortfolio(userId).map(p -> ResponseEntity.ok(PortfolioResponse.from(p)));
    }
}
