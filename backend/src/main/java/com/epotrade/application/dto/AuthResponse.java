package com.epotrade.application.dto;

public record AuthResponse(String accessToken, String refreshToken, long expiresIn, String username) {}
