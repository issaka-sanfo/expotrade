package com.expotrade.domain.port.out;

import com.expotrade.domain.model.BrokerAccount;
import com.expotrade.domain.model.enums.BrokerAccountStatus;
import com.expotrade.domain.model.enums.BrokerType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BrokerAccountRepository {
    BrokerAccount save(BrokerAccount account);
    Optional<BrokerAccount> findById(UUID id);
    List<BrokerAccount> findByUserId(UUID userId);
    Optional<BrokerAccount> findByUserIdAndBrokerType(UUID userId, BrokerType brokerType);
    Optional<BrokerAccount> findByUserIdAndBrokerTypeAndStatus(UUID userId, BrokerType brokerType, BrokerAccountStatus status);
    void deleteById(UUID id);
}
