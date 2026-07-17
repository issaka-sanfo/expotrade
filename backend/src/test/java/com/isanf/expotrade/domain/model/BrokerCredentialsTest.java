package com.isanf.expotrade.domain.model;

import com.isanf.expotrade.domain.model.enums.BrokerType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BrokerCredentialsTest {

    @Test
    void fromCopiesBrokerAccountCredentialFields() {
        BrokerAccount account = BrokerAccount.create(UUID.randomUUID(), BrokerType.IBKR,
                "acct-1", "key", "secret", "token");

        BrokerCredentials credentials = BrokerCredentials.from(account);

        assertEquals(BrokerType.IBKR, credentials.brokerType());
        assertEquals("acct-1", credentials.accountId());
        assertEquals("key", credentials.apiKey());
        assertEquals("secret", credentials.apiSecret());
        assertEquals("token", credentials.accessToken());
    }
}
