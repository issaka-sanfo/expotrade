package com.isanf.expotrade.application.dto;

import com.isanf.expotrade.domain.model.enums.BrokerType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BrokerAccountRequest(
        @NotNull BrokerType brokerType,
        @NotBlank String accountId,
        String apiKey,
        String apiSecret,
        String accessToken
) {}
