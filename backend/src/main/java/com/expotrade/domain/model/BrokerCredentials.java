package com.expotrade.domain.model;

import com.expotrade.domain.model.enums.BrokerType;

/**
 * Decrypted broker credentials passed to broker adapters at execution time.
 */
public record BrokerCredentials(
        BrokerType brokerType,
        String accountId,
        String apiKey,
        String apiSecret,
        String accessToken
) {
    public static BrokerCredentials from(BrokerAccount account) {
        return new BrokerCredentials(
                account.brokerType(), account.accountId(),
                account.apiKey(), account.apiSecret(), account.accessToken()
        );
    }
}
