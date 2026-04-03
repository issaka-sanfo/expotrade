package com.expotrade.infrastructure.persistence.repository;

import com.expotrade.infrastructure.persistence.entity.BrokerAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaBrokerAccountRepository extends JpaRepository<BrokerAccountEntity, UUID> {
    List<BrokerAccountEntity> findByUserId(UUID userId);
    Optional<BrokerAccountEntity> findByUserIdAndBrokerType(UUID userId, String brokerType);
    Optional<BrokerAccountEntity> findByUserIdAndBrokerTypeAndStatus(UUID userId, String brokerType, String status);
}
