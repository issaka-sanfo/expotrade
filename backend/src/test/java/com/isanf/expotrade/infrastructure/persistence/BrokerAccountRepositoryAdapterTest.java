package com.isanf.expotrade.infrastructure.persistence;

import com.isanf.expotrade.domain.model.BrokerAccount;
import com.isanf.expotrade.domain.model.enums.BrokerAccountStatus;
import com.isanf.expotrade.domain.model.enums.BrokerType;
import com.isanf.expotrade.infrastructure.persistence.entity.BrokerAccountEntity;
import com.isanf.expotrade.infrastructure.persistence.repository.JpaBrokerAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BrokerAccountRepositoryAdapterTest {

    private JpaBrokerAccountRepository jpaRepository;
    private BrokerAccountRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        jpaRepository = mock(JpaBrokerAccountRepository.class);
        adapter = new BrokerAccountRepositoryAdapter(jpaRepository);
    }

    @Test
    void savesDomainAccountAsEntityAndReturnsSavedDomainAccount() {
        BrokerAccount account = brokerAccount();
        when(jpaRepository.save(any(BrokerAccountEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        BrokerAccount saved = adapter.save(account);

        assertEquals(account, saved);
        ArgumentCaptor<BrokerAccountEntity> captor = ArgumentCaptor.forClass(BrokerAccountEntity.class);
        verify(jpaRepository).save(captor.capture());
        assertEquals(account, captor.getValue().toDomain());
    }

    @Test
    void findsAccountByIdAndMapsEntityToDomain() {
        BrokerAccount account = brokerAccount();
        when(jpaRepository.findById(account.id()))
                .thenReturn(Optional.of(BrokerAccountEntity.fromDomain(account)));

        Optional<BrokerAccount> found = adapter.findById(account.id());

        assertTrue(found.isPresent());
        assertEquals(account, found.get());
    }

    @Test
    void findsAccountsByUserIdAndMapsEntitiesToDomain() {
        BrokerAccount account = brokerAccount();
        when(jpaRepository.findByUserId(account.userId()))
                .thenReturn(List.of(BrokerAccountEntity.fromDomain(account)));

        List<BrokerAccount> accounts = adapter.findByUserId(account.userId());

        assertEquals(List.of(account), accounts);
    }

    @Test
    void findsByUserIdAndBrokerTypeUsingEnumName() {
        BrokerAccount account = brokerAccount();
        when(jpaRepository.findByUserIdAndBrokerType(account.userId(), BrokerType.IBKR.name()))
                .thenReturn(Optional.of(BrokerAccountEntity.fromDomain(account)));

        Optional<BrokerAccount> found = adapter.findByUserIdAndBrokerType(account.userId(), BrokerType.IBKR);

        assertEquals(Optional.of(account), found);
    }

    @Test
    void findsByUserIdBrokerTypeAndStatusUsingEnumNames() {
        BrokerAccount account = brokerAccount();
        when(jpaRepository.findByUserIdAndBrokerTypeAndStatus(
                account.userId(), BrokerType.IBKR.name(), BrokerAccountStatus.ACTIVE.name()))
                .thenReturn(Optional.of(BrokerAccountEntity.fromDomain(account)));

        Optional<BrokerAccount> found = adapter.findByUserIdAndBrokerTypeAndStatus(
                account.userId(), BrokerType.IBKR, BrokerAccountStatus.ACTIVE);

        assertEquals(Optional.of(account), found);
    }

    @Test
    void deletesByIdThroughJpaRepository() {
        UUID accountId = UUID.randomUUID();

        adapter.deleteById(accountId);

        verify(jpaRepository).deleteById(accountId);
    }

    private static BrokerAccount brokerAccount() {
        Instant createdAt = Instant.parse("2026-07-18T08:00:00Z");
        Instant updatedAt = Instant.parse("2026-07-18T09:00:00Z");
        return new BrokerAccount(
                UUID.randomUUID(),
                UUID.randomUUID(),
                BrokerType.IBKR,
                "account-1",
                "encrypted-api-key",
                "encrypted-api-secret",
                "encrypted-access-token",
                BrokerAccountStatus.ACTIVE,
                createdAt,
                updatedAt
        );
    }
}
