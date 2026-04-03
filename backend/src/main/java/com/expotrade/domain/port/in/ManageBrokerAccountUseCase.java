package com.expotrade.domain.port.in;

import com.expotrade.domain.model.BrokerAccount;
import com.expotrade.domain.model.enums.BrokerType;

import java.util.List;
import java.util.UUID;

public interface ManageBrokerAccountUseCase {

    record LinkBrokerAccountCommand(
            UUID userId,
            BrokerType brokerType,
            String accountId,
            String apiKey,
            String apiSecret,
            String accessToken
    ) {}

    record UpdateBrokerAccountCommand(
            UUID accountId,
            UUID userId,
            String apiKey,
            String apiSecret,
            String accessToken
    ) {}

    BrokerAccount linkAccount(LinkBrokerAccountCommand command);
    BrokerAccount updateAccount(UpdateBrokerAccountCommand command);
    void unlinkAccount(UUID accountId, UUID userId);
    List<BrokerAccount> getAccounts(UUID userId);
    BrokerAccount verifyAccount(UUID accountId, UUID userId);
}
