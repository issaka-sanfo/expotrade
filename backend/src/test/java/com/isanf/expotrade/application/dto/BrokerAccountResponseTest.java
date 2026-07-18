package com.isanf.expotrade.application.dto;

import com.isanf.expotrade.domain.model.BrokerAccount;
import com.isanf.expotrade.domain.model.enums.BrokerAccountStatus;
import com.isanf.expotrade.domain.model.enums.BrokerType;
import org.junit.jupiter.api.Test;

import java.lang.reflect.RecordComponent;
import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BrokerAccountResponseTest {

    @Test
    void mapsPublicAccountFieldsWithoutExposingCredentials() {
        BrokerAccount account = new BrokerAccount(
                UUID.randomUUID(),
                UUID.randomUUID(),
                BrokerType.IBKR,
                "account-1",
                "api-key",
                "api-secret",
                "access-token",
                BrokerAccountStatus.ACTIVE,
                Instant.parse("2026-07-18T08:00:00Z"),
                Instant.parse("2026-07-18T09:00:00Z")
        );

        BrokerAccountResponse response = BrokerAccountResponse.from(account);

        assertEquals(account.id(), response.id());
        assertEquals(BrokerType.IBKR, response.brokerType());
        assertEquals("account-1", response.accountId());
        assertTrue(response.hasApiKey());
        assertTrue(response.hasApiSecret());
        assertTrue(response.hasAccessToken());
        assertEquals(BrokerAccountStatus.ACTIVE, response.status());
        assertFalse(hasRecordComponent("apiKey"));
        assertFalse(hasRecordComponent("apiSecret"));
        assertFalse(hasRecordComponent("accessToken"));
    }

    private static boolean hasRecordComponent(String componentName) {
        return Arrays.stream(BrokerAccountResponse.class.getRecordComponents())
                .map(RecordComponent::getName)
                .anyMatch(componentName::equals);
    }
}
