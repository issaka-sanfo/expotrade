package com.expotrade.domain.model;

import com.expotrade.domain.model.enums.BrokerAccountStatus;
import com.expotrade.domain.model.enums.BrokerType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BrokerAccountTest {

    @Test
    void createBuildsPendingVerificationAccountWithGeneratedIdentity() {
        UUID userId = UUID.randomUUID();

        BrokerAccount account = BrokerAccount.create(userId, BrokerType.ETORO,
                "acct-1", "key", "secret", "token");

        assertNotNull(account.id());
        assertEquals(userId, account.userId());
        assertEquals(BrokerAccountStatus.PENDING_VERIFICATION, account.status());
        assertEquals("acct-1", account.accountId());
        assertNotNull(account.createdAt());
        assertNotNull(account.updatedAt());
    }

    @Test
    void withStatusPreservesAccountDataAndUpdatesStatus() {
        BrokerAccount account = account();

        BrokerAccount active = account.withStatus(BrokerAccountStatus.ACTIVE);

        assertEquals(account.id(), active.id());
        assertEquals(account.apiKey(), active.apiKey());
        assertEquals(BrokerAccountStatus.ACTIVE, active.status());
        assertTrue(!active.updatedAt().isBefore(account.updatedAt()));
    }

    @Test
    void withCredentialsPreservesAccountDataAndReplacesSecrets() {
        BrokerAccount account = account();

        BrokerAccount updated = account.withCredentials("new-key", "new-secret", "new-token");

        assertEquals(account.id(), updated.id());
        assertEquals(account.status(), updated.status());
        assertEquals("new-key", updated.apiKey());
        assertEquals("new-secret", updated.apiSecret());
        assertEquals("new-token", updated.accessToken());
        assertTrue(!updated.updatedAt().isBefore(account.updatedAt()));
    }

    private BrokerAccount account() {
        return BrokerAccount.create(UUID.randomUUID(), BrokerType.ETORO,
                "acct-1", "key", "secret", "token");
    }
}
