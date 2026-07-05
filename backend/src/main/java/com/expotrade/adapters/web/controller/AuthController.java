package com.expotrade.adapters.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login() {
        return ResponseEntity.status(HttpStatus.GONE)
                .body(Map.of("message", "Password login is handled by Keycloak"));
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register() {
        return ResponseEntity.status(HttpStatus.GONE)
                .body(Map.of("message", "Registration is handled by Keycloak"));
    }
}
