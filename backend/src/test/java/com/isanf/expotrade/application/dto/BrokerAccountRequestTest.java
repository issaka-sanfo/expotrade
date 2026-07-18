package com.isanf.expotrade.application.dto;

import com.isanf.expotrade.domain.model.enums.BrokerType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class BrokerAccountRequestTest {
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
    void rejectsMissingBrokerIdentityFields() {
        BrokerAccountRequest request = new BrokerAccountRequest(null, "", null, null, null);

        Set<ConstraintViolation<BrokerAccountRequest>> violations = validator.validate(request);

        assertHasViolationOn(violations, "brokerType");
        assertHasViolationOn(violations, "accountId");
    }

    @Test
    void acceptsValidPayload() {
        BrokerAccountRequest request = new BrokerAccountRequest(
                BrokerType.IBKR,
                "account-1",
                "api-key",
                "api-secret",
                "access-token"
        );

        assertTrue(validator.validate(request).isEmpty());
    }

    private static void assertHasViolationOn(Set<ConstraintViolation<BrokerAccountRequest>> violations, String propertyName) {
        assertTrue(
                violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals(propertyName)),
                "Expected a validation error on " + propertyName
        );
    }
}
