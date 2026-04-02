package com.epotrade.adapters.web.controller;

import com.epotrade.application.dto.AuthRequest;
import com.epotrade.application.dto.AuthResponse;
import com.epotrade.application.dto.RegisterRequest;
import com.epotrade.application.service.AuthService;
import com.epotrade.domain.model.User;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;
    public AuthController(AuthService authService) { this.authService = authService; }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterRequest request) {
        User user = authService.register(request);
        return ResponseEntity.ok(Map.of("message", "Registration successful", "userId", user.id().toString()));
    }
}
