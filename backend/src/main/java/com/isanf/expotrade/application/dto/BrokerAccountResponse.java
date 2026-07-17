package com.isanf.expotrade.application.dto;

import com.isanf.expotrade.domain.model.BrokerAccount;
import com.isanf.expotrade.domain.model.enums.BrokerAccountStatus;
import com.isanf.expotrade.domain.model.enums.BrokerType;

import java.time.Instant;
import java.util.UUID;

public record BrokerAccountResponse(
        UUID id,
        BrokerType brokerType,
        String accountId,
        boolean hasApiKey,
        boolean hasApiSecret,
        boolean hasAccessToken,
        BrokerAccountStatus status,
        Instant createdAt,
        Instant updatedAt
) {
    public static BrokerAccountResponse from(BrokerAccount account) {
        return new BrokerAccountResponse(
                account.id(), account.brokerType(), account.accountId(),
                account.apiKey() != null, account.apiSecret() != null,
                account.accessToken() != null, account.status(),
                account.createdAt(), account.updatedAt()
        );
    }
}
