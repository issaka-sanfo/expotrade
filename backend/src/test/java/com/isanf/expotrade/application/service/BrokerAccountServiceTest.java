package com.isanf.expotrade.application.service;

import com.isanf.expotrade.domain.model.BrokerAccount;
import com.isanf.expotrade.domain.model.BrokerCredentials;
import com.isanf.expotrade.domain.model.enums.BrokerAccountStatus;
import com.isanf.expotrade.domain.model.enums.BrokerType;
import com.isanf.expotrade.domain.port.in.ManageBrokerAccountUseCase.LinkBrokerAccountCommand;
import com.isanf.expotrade.domain.port.out.BrokerAccountRepository;
import com.isanf.expotrade.domain.port.out.BrokerPort;
import com.isanf.expotrade.infrastructure.security.CredentialEncryptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BrokerAccountServiceTest {

    private BrokerAccountRepository repository;
    private BrokerPort broker;
    private CredentialEncryptor encryptor;
    private BrokerAccountService service;

    @BeforeEach
    void setUp() {
        repository = mock(BrokerAccountRepository.class);
        broker = mock(BrokerPort.class);
        encryptor = new CredentialEncryptor("MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=");
        service = new BrokerAccountService(repository, encryptor, Map.of(BrokerType.IBKR.name(), broker));
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
        BrokerAccount account = encryptedAccount(userId, BrokerAccountStatus.PENDING_VERIFICATION);
        when(repository.findById(account.id())).thenReturn(Optional.of(account));
        when(repository.save(any(BrokerAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(broker.verifyCredentials(any(BrokerCredentials.class))).thenReturn(Mono.just(true));

        BrokerAccount verified = service.verifyAccount(account.id(), userId);

        assertEquals(BrokerAccountStatus.ACTIVE, verified.status());
        ArgumentCaptor<BrokerCredentials> credentials = ArgumentCaptor.forClass(BrokerCredentials.class);
        verify(broker).verifyCredentials(credentials.capture());
        assertEquals(BrokerType.IBKR, credentials.getValue().brokerType());
        assertEquals("acct-1", credentials.getValue().accountId());
        assertEquals("api-key", credentials.getValue().apiKey());
        assertEquals("api-secret", credentials.getValue().apiSecret());
        assertEquals("token", credentials.getValue().accessToken());
    }

    @Test
    void verifyAccountMarksAccountVerificationFailedWhenBrokerRejectsCredentials() {
        UUID userId = UUID.randomUUID();
        BrokerAccount account = encryptedAccount(userId, BrokerAccountStatus.PENDING_VERIFICATION);
        when(repository.findById(account.id())).thenReturn(Optional.of(account));
        when(repository.save(any(BrokerAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(broker.verifyCredentials(any(BrokerCredentials.class))).thenReturn(Mono.just(false));

        BrokerAccount verified = service.verifyAccount(account.id(), userId);

        assertEquals(BrokerAccountStatus.VERIFICATION_FAILED, verified.status());
    }

    @Test
    void verifyAccountMarksAccountVerificationFailedWhenCredentialsAreMissing() {
        UUID userId = UUID.randomUUID();
        BrokerAccount account = new BrokerAccount(
                UUID.randomUUID(), userId, BrokerType.IBKR, "acct-1",
                null, null, null,
                BrokerAccountStatus.PENDING_VERIFICATION,
                Instant.now(), Instant.now());
        when(repository.findById(account.id())).thenReturn(Optional.of(account));
        when(repository.save(any(BrokerAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BrokerAccount verified = service.verifyAccount(account.id(), userId);

        assertEquals(BrokerAccountStatus.VERIFICATION_FAILED, verified.status());
        verify(broker, never()).verifyCredentials(any(BrokerCredentials.class));
        verify(repository).save(argThat(saved ->
                saved.id().equals(account.id())
                        && saved.userId().equals(userId)
                        && saved.status() == BrokerAccountStatus.VERIFICATION_FAILED));
    }

    @Test
    void unlinkAccountMarksAccountInactiveInsteadOfDeletingIt() {
        UUID userId = UUID.randomUUID();
        BrokerAccount account = account(userId, BrokerAccountStatus.ACTIVE);
        when(repository.findById(account.id())).thenReturn(Optional.of(account));
        when(repository.save(any(BrokerAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.unlinkAccount(account.id(), userId);

        verify(repository).save(argThat(saved ->
                saved.id().equals(account.id())
                        && saved.userId().equals(userId)
                        && saved.status() == BrokerAccountStatus.INACTIVE));
        verify(repository, never()).deleteById(account.id());
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

    private BrokerAccount encryptedAccount(UUID userId, BrokerAccountStatus status) {
        return BrokerAccount.create(userId, BrokerType.IBKR, "acct-1",
                        encryptor.encrypt("api-key"), encryptor.encrypt("api-secret"), encryptor.encrypt("token"))
                .withStatus(status);
    }
}
