package com.isanf.expotrade.application.service;

import com.isanf.expotrade.domain.model.BrokerAccount;
import com.isanf.expotrade.domain.model.BrokerCredentials;
import com.isanf.expotrade.domain.model.enums.BrokerAccountStatus;
import com.isanf.expotrade.domain.model.enums.BrokerType;
import com.isanf.expotrade.domain.port.in.ManageBrokerAccountUseCase;
import com.isanf.expotrade.domain.port.out.BrokerAccountRepository;
import com.isanf.expotrade.domain.port.out.BrokerPort;
import com.isanf.expotrade.infrastructure.security.CredentialEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class BrokerAccountService implements ManageBrokerAccountUseCase {

    private static final Logger log = LoggerFactory.getLogger(BrokerAccountService.class);

    private final BrokerAccountRepository accountRepository;
    private final CredentialEncryptor encryptor;
    private final Map<String, BrokerPort> brokerPorts;

    public BrokerAccountService(BrokerAccountRepository accountRepository,
                                CredentialEncryptor encryptor,
                                Map<String, BrokerPort> brokerPorts) {
        this.accountRepository = accountRepository;
        this.encryptor = encryptor;
        this.brokerPorts = brokerPorts;
    }

    @Override
    public BrokerAccount linkAccount(LinkBrokerAccountCommand command) {
        accountRepository.findByUserIdAndBrokerType(command.userId(), command.brokerType())
                .ifPresent(existing -> {
                    throw new IllegalStateException("Broker account already linked for " + command.brokerType());
                });

        BrokerAccount account = BrokerAccount.create(
                command.userId(), command.brokerType(), command.accountId(),
                encryptor.encrypt(command.apiKey()),
                encryptor.encrypt(command.apiSecret()),
                encryptor.encrypt(command.accessToken())
        );

        BrokerAccount saved = accountRepository.save(account);
        log.info("Broker account linked: {} for user {}", command.brokerType(), command.userId());
        return saved;
    }

    @Override
    public BrokerAccount updateAccount(UpdateBrokerAccountCommand command) {
        BrokerAccount account = accountRepository.findById(command.accountId())
                .filter(a -> a.userId().equals(command.userId()))
                .orElseThrow(() -> new IllegalArgumentException("Broker account not found"));

        BrokerAccount updated = account.withCredentials(
                encryptor.encrypt(command.apiKey()),
                encryptor.encrypt(command.apiSecret()),
                encryptor.encrypt(command.accessToken())
        );

        BrokerAccount saved = accountRepository.save(updated);
        log.info("Broker account updated: {}", account.brokerType());
        return saved;
    }

    @Override
    public void unlinkAccount(UUID accountId, UUID userId) {
        BrokerAccount account = accountRepository.findById(accountId)
                .filter(a -> a.userId().equals(userId))
                .orElseThrow(() -> new IllegalArgumentException("Broker account not found"));

        accountRepository.save(account.withStatus(BrokerAccountStatus.INACTIVE));
        log.info("Broker account unlinked: {} for user {}", account.brokerType(), userId);
    }

    @Override
    public List<BrokerAccount> getAccounts(UUID userId) {
        return accountRepository.findByUserId(userId);
    }

    @Override
    public BrokerAccount verifyAccount(UUID accountId, UUID userId) {
        BrokerAccount account = accountRepository.findById(accountId)
                .filter(a -> a.userId().equals(userId))
                .orElseThrow(() -> new IllegalArgumentException("Broker account not found"));

        BrokerCredentials credentials = new BrokerCredentials(
                account.brokerType(), account.accountId(),
                encryptor.decrypt(account.apiKey()),
                encryptor.decrypt(account.apiSecret()),
                encryptor.decrypt(account.accessToken())
        );

        BrokerAccountStatus status = verifyCredentials(credentials)
                ? BrokerAccountStatus.ACTIVE
                : BrokerAccountStatus.VERIFICATION_FAILED;
        BrokerAccount verified = account.withStatus(status);
        BrokerAccount saved = accountRepository.save(verified);
        log.info("Broker account verification result: {} for user {} status {}", account.brokerType(), userId, status);
        return saved;
    }

    private boolean verifyCredentials(BrokerCredentials credentials) {
        if (!hasCompleteCredentials(credentials)) {
            return false;
        }

        BrokerPort broker = brokerPorts.get(credentials.brokerType().name());
        if (broker == null) {
            return false;
        }

        try {
            return Boolean.TRUE.equals(broker.verifyCredentials(credentials).block());
        } catch (RuntimeException ex) {
            log.warn("Broker account credential verification failed for {}", credentials.brokerType());
            return false;
        }
    }

    private boolean hasCompleteCredentials(BrokerCredentials credentials) {
        return hasText(credentials.accountId())
                && hasText(credentials.apiKey())
                && hasText(credentials.apiSecret())
                && hasText(credentials.accessToken());
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    /**
     * Resolves decrypted credentials for a user's active broker account.
     * Used by OrderService when placing orders.
     */
    public BrokerCredentials resolveCredentials(UUID userId, BrokerType brokerType) {
        BrokerAccount account = accountRepository
                .findByUserIdAndBrokerTypeAndStatus(userId, brokerType, BrokerAccountStatus.ACTIVE)
                .orElseThrow(() -> new IllegalStateException(
                        "No active " + brokerType + " account linked. Please link your broker account first."));

        return new BrokerCredentials(
                account.brokerType(), account.accountId(),
                encryptor.decrypt(account.apiKey()),
                encryptor.decrypt(account.apiSecret()),
                encryptor.decrypt(account.accessToken())
        );
    }
}
