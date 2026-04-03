package com.expotrade.infrastructure.persistence.entity;

import com.expotrade.domain.model.BrokerAccount;
import com.expotrade.domain.model.enums.BrokerAccountStatus;
import com.expotrade.domain.model.enums.BrokerType;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "broker_accounts", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"userId", "brokerType"})
})
public class BrokerAccountEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BrokerType brokerType;

    @Column(nullable = false)
    private String accountId;

    @Column(length = 512)
    private String apiKey;

    @Column(length = 1024)
    private String apiSecret;

    @Column(length = 2048)
    private String accessToken;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BrokerAccountStatus status;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    protected BrokerAccountEntity() {}

    public static BrokerAccountEntity fromDomain(BrokerAccount a) {
        BrokerAccountEntity e = new BrokerAccountEntity();
        e.id = a.id();
        e.userId = a.userId();
        e.brokerType = a.brokerType();
        e.accountId = a.accountId();
        e.apiKey = a.apiKey();
        e.apiSecret = a.apiSecret();
        e.accessToken = a.accessToken();
        e.status = a.status();
        e.createdAt = a.createdAt();
        e.updatedAt = a.updatedAt();
        return e;
    }

    public BrokerAccount toDomain() {
        return new BrokerAccount(id, userId, brokerType, accountId,
                apiKey, apiSecret, accessToken, status, createdAt, updatedAt);
    }
}
