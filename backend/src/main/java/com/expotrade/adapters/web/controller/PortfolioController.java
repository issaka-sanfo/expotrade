package com.expotrade.adapters.web.controller;

import com.expotrade.application.dto.PortfolioResponse;
import com.expotrade.application.service.PortfolioService;
import com.expotrade.config.AuthenticatedUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/portfolio")
public class PortfolioController {
    private final PortfolioService portfolioService;
    public PortfolioController(PortfolioService portfolioService) { this.portfolioService = portfolioService; }

    @GetMapping
    public Mono<ResponseEntity<PortfolioResponse>> getPortfolio(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = AuthenticatedUser.id(jwt);
        return portfolioService.getPortfolio(userId).map(p -> ResponseEntity.ok(PortfolioResponse.from(p)));
    }
}
