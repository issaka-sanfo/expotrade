package com.expotrade.domain.model;

import com.expotrade.domain.model.enums.BrokerAccountStatus;
import com.expotrade.domain.model.enums.BrokerType;

import java.time.Instant;
import java.util.UUID;

public record BrokerAccount(
        UUID id,
        UUID userId,
        BrokerType brokerType,
        String accountId,
        String apiKey,
        String apiSecret,
        String accessToken,
        BrokerAccountStatus status,
        Instant createdAt,
        Instant updatedAt
) {
    public static BrokerAccount create(UUID userId, BrokerType brokerType, String accountId,
                                       String apiKey, String apiSecret, String accessToken) {
        return new BrokerAccount(
                UUID.randomUUID(), userId, brokerType, accountId,
                apiKey, apiSecret, accessToken,
                BrokerAccountStatus.PENDING_VERIFICATION,
                Instant.now(), Instant.now()
        );
    }

    public BrokerAccount withStatus(BrokerAccountStatus newStatus) {
        return new BrokerAccount(id, userId, brokerType, accountId,
                apiKey, apiSecret, accessToken, newStatus, createdAt, Instant.now());
    }

    public BrokerAccount withCredentials(String newApiKey, String newApiSecret, String newAccessToken) {
        return new BrokerAccount(id, userId, brokerType, accountId,
                newApiKey, newApiSecret, newAccessToken, status, createdAt, Instant.now());
    }
}
