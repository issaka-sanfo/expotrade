package com.isanf.expotrade.application.dto;

import com.isanf.expotrade.domain.model.enums.BrokerType;
import com.isanf.expotrade.domain.model.enums.OrderSide;
import com.isanf.expotrade.domain.model.enums.OrderType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderRequestTest {
    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidator() {
        validatorFactory.close();
    }

    @Test
    void rejectsMissingRequiredTradingFields() {
        OrderRequest request = new OrderRequest(
                "",
                null,
                null,
                BigDecimal.ZERO,
                null,
                null,
                null,
                null,
                null
        );

        Set<ConstraintViolation<OrderRequest>> violations = validator.validate(request);

        assertHasViolationOn(violations, "symbol");
        assertHasViolationOn(violations, "side");
        assertHasViolationOn(violations, "type");
        assertHasViolationOn(violations, "quantity");
        assertHasViolationOn(violations, "brokerType");
    }

    @Test
    void acceptsValidPayload() {
        OrderRequest request = new OrderRequest(
                "AAPL",
                OrderSide.BUY,
                OrderType.MARKET,
                BigDecimal.ONE,
                null,
                null,
                null,
                BrokerType.IBKR,
                "strategy-1"
        );

        assertTrue(validator.validate(request).isEmpty());
    }

    private static void assertHasViolationOn(Set<ConstraintViolation<OrderRequest>> violations, String propertyName) {
        assertTrue(
                violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals(propertyName)),
                "Expected a validation error on " + propertyName
        );
    }
}
