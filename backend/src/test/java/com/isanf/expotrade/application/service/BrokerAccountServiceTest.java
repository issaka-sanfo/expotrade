package com.isanf.expotrade.application.service;

import com.isanf.expotrade.domain.model.BrokerAccount;
import com.isanf.expotrade.domain.model.BrokerCredentials;
import com.isanf.expotrade.domain.model.enums.BrokerAccountStatus;
import com.isanf.expotrade.domain.model.enums.BrokerType;
import com.isanf.expotrade.domain.port.in.ManageBrokerAccountUseCase.LinkBrokerAccountCommand;
import com.isanf.expotrade.domain.port.out.BrokerAccountRepository;
import com.isanf.expotrade.infrastructure.security.CredentialEncryptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BrokerAccountServiceTest {

    private BrokerAccountRepository repository;
    private CredentialEncryptor encryptor;
    private BrokerAccountService service;

    @BeforeEach
    void setUp() {
        repository = mock(BrokerAccountRepository.class);
        encryptor = new CredentialEncryptor("MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=");
        service = new BrokerAccountService(repository, encryptor);
    }

    @Test
    void linkAccountEncryptsCredentialsAndRejectsDuplicates() {
        UUID userId = UUID.randomUUID();
        when(repository.findByUserIdAndBrokerType(userId, BrokerType.IBKR)).thenReturn(Optional.empty());
        when(repository.save(any(BrokerAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BrokerAccount linked = service.linkAccount(new LinkBrokerAccountCommand(
                userId, BrokerType.IBKR, "acct-1", "key", "secret", "token"));

        assertNotEquals("key", linked.apiKey());
        assertEquals("key", encryptor.decrypt(linked.apiKey()));
        assertEquals(BrokerAccountStatus.PENDING_VERIFICATION, linked.status());
        verify(repository).save(linked);

        when(repository.findByUserIdAndBrokerType(userId, BrokerType.IBKR)).thenReturn(Optional.of(linked));
        assertThrows(IllegalStateException.class, () -> service.linkAccount(new LinkBrokerAccountCommand(
                userId, BrokerType.IBKR, "acct-1", "key", "secret", "token")));
    }

    @Test
    void verifyAccountMarksAccountActiveForOwner() {
        UUID userId = UUID.randomUUID();
        BrokerAccount account = account(userId, BrokerAccountStatus.PENDING_VERIFICATION);
        when(repository.findById(account.id())).thenReturn(Optional.of(account));
        when(repository.save(any(BrokerAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BrokerAccount verified = service.verifyAccount(account.id(), userId);

        assertEquals(BrokerAccountStatus.ACTIVE, verified.status());
    }

    @Test
    void resolveCredentialsDecryptsActiveAccount() {
        UUID userId = UUID.randomUUID();
        BrokerAccount encrypted = BrokerAccount.create(userId, BrokerType.IBKR, "acct-1",
                encryptor.encrypt("key"), encryptor.encrypt("secret"), encryptor.encrypt("access"))
                .withStatus(BrokerAccountStatus.ACTIVE);
        when(repository.findByUserIdAndBrokerTypeAndStatus(userId, BrokerType.IBKR, BrokerAccountStatus.ACTIVE))
                .thenReturn(Optional.of(encrypted));

        BrokerCredentials credentials = service.resolveCredentials(userId, BrokerType.IBKR);

        assertEquals("key", credentials.apiKey());
        assertEquals("secret", credentials.apiSecret());
        assertEquals("access", credentials.accessToken());
    }

    @Test
    void getAccountsReturnsRepositoryAccounts() {
        UUID userId = UUID.randomUUID();
        List<BrokerAccount> accounts = List.of(account(userId, BrokerAccountStatus.ACTIVE));
        when(repository.findByUserId(userId)).thenReturn(accounts);

        assertSame(accounts, service.getAccounts(userId));
    }

    private BrokerAccount account(UUID userId, BrokerAccountStatus status) {
        return BrokerAccount.create(userId, BrokerType.IBKR, "acct-1", "api-key", "api-secret", "token")
                .withStatus(status);
    }
}
