package com.epotrade.domain.model;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record User(
        UUID id,
        String username,
        String email,
        String passwordHash,
        Set<String> roles,
        boolean enabled,
        Instant createdAt
) {}
