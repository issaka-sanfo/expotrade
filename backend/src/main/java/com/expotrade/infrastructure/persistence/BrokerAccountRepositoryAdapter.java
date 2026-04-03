package com.expotrade.infrastructure.persistence;

import com.expotrade.domain.model.BrokerAccount;
import com.expotrade.domain.model.enums.BrokerAccountStatus;
import com.expotrade.domain.model.enums.BrokerType;
import com.expotrade.domain.port.out.BrokerAccountRepository;
import com.expotrade.infrastructure.persistence.entity.BrokerAccountEntity;
import com.expotrade.infrastructure.persistence.repository.JpaBrokerAccountRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class BrokerAccountRepositoryAdapter implements BrokerAccountRepository {

    private final JpaBrokerAccountRepository jpaRepo;

    public BrokerAccountRepositoryAdapter(JpaBrokerAccountRepository jpaRepo) {
        this.jpaRepo = jpaRepo;
    }

    @Override
    public BrokerAccount save(BrokerAccount account) {
        return jpaRepo.save(BrokerAccountEntity.fromDomain(account)).toDomain();
    }

    @Override
    public Optional<BrokerAccount> findById(UUID id) {
        return jpaRepo.findById(id).map(BrokerAccountEntity::toDomain);
    }

    @Override
    public List<BrokerAccount> findByUserId(UUID userId) {
        return jpaRepo.findByUserId(userId).stream().map(BrokerAccountEntity::toDomain).toList();
    }

    @Override
    public Optional<BrokerAccount> findByUserIdAndBrokerType(UUID userId, BrokerType brokerType) {
        return jpaRepo.findByUserIdAndBrokerType(userId, brokerType.name()).map(BrokerAccountEntity::toDomain);
    }

    @Override
    public Optional<BrokerAccount> findByUserIdAndBrokerTypeAndStatus(UUID userId, BrokerType brokerType, BrokerAccountStatus status) {
        return jpaRepo.findByUserIdAndBrokerTypeAndStatus(userId, brokerType.name(), status.name()).map(BrokerAccountEntity::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepo.deleteById(id);
    }
}
