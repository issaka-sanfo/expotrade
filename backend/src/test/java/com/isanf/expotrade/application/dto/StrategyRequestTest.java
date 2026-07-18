package com.isanf.expotrade.application.dto;

import com.isanf.expotrade.domain.model.enums.BrokerType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class StrategyRequestTest {
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
    void rejectsMissingStrategyDefinitionFields() {
        StrategyRequest request = new StrategyRequest(
                "",
                "",
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        Set<ConstraintViolation<StrategyRequest>> violations = validator.validate(request);

        assertHasViolationOn(violations, "name");
        assertHasViolationOn(violations, "type");
        assertHasViolationOn(violations, "symbols");
        assertHasViolationOn(violations, "brokerType");
    }

    @Test
    void acceptsValidPayload() {
        StrategyRequest request = new StrategyRequest(
                "Momentum",
                "MOVING_AVERAGE",
                List.of("AAPL"),
                BrokerType.IBKR,
                BigDecimal.valueOf(1_000),
                BigDecimal.valueOf(2),
                BigDecimal.valueOf(4),
                BigDecimal.valueOf(10),
                null
        );

        assertTrue(validator.validate(request).isEmpty());
    }

    private static void assertHasViolationOn(Set<ConstraintViolation<StrategyRequest>> violations, String propertyName) {
        assertTrue(
                violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals(propertyName)),
                "Expected a validation error on " + propertyName
        );
    }
}
