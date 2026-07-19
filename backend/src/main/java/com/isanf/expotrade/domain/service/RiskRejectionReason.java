package com.isanf.expotrade.domain.service;

public enum RiskRejectionReason {
    MAX_POSITION_SIZE_EXCEEDED,
    MAX_DRAWDOWN_EXCEEDED,
    INSUFFICIENT_CASH_BALANCE,
    MISSING_PORTFOLIO,
    MISSING_STRATEGY_CONFIG
}
