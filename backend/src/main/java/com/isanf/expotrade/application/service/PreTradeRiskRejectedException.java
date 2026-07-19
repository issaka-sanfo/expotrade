package com.isanf.expotrade.application.service;

import com.isanf.expotrade.domain.service.RiskRejectionReason;

public class PreTradeRiskRejectedException extends RuntimeException {

    private static final String MESSAGE = "Pre-trade risk check rejected the order";

    private final RiskRejectionReason reason;

    public PreTradeRiskRejectedException(RiskRejectionReason reason) {
        super(MESSAGE);
        this.reason = reason;
    }

    public RiskRejectionReason reason() {
        return reason;
    }

    public String code() {
        return reason.name();
    }
}
