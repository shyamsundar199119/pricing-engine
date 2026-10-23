package eu.moonpark.pricingengine.validation;

import eu.moonpark.pricingengine.dto.RateRequest;
import eu.moonpark.pricingengine.model.TariffZone;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RateRequestTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        validatorFactory.close();
    }

    @Test
    void shouldPassValidationForValidRequest() {
        RateRequest request = new RateRequest(
                LocalDateTime.of(2026, 10, 26, 10, 0),
                LocalDateTime.of(2026, 10, 26, 11, 0),
                TariffZone.M1
        );

        Set<ConstraintViolation<RateRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailWhenStartTimeIsNull() {
        RateRequest request = new RateRequest(
                null,
                LocalDateTime.of(2026, 10, 26, 11, 0),
                TariffZone.M1
        );

        Set<ConstraintViolation<RateRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals("Start time is required", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailWhenEndTimeIsNull() {
        RateRequest request = new RateRequest(
                LocalDateTime.of(2026, 10, 26, 10, 0),
                null,
                TariffZone.M1
        );

        Set<ConstraintViolation<RateRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals("End time is required", violations.iterator().next().getMessage());
    }

    @Test
    void shouldFailWhenTariffZoneIsNull() {
        RateRequest request = new RateRequest(
                LocalDateTime.of(2026, 10, 26, 10, 0),
                LocalDateTime.of(2026, 10, 26, 11, 0),
                null
        );

        Set<ConstraintViolation<RateRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals("Tariff zone is required", violations.iterator().next().getMessage());
    }
}
