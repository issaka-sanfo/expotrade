package com.isanf.expotrade.domain.service;

public record RiskDecision(
        boolean accepted,
        RiskRejectionReason rejectionReason
) {

    public static RiskDecision accept() {
        return new RiskDecision(true, null);
    }

    public static RiskDecision rejected(RiskRejectionReason reason) {
        return new RiskDecision(false, reason);
    }
}
