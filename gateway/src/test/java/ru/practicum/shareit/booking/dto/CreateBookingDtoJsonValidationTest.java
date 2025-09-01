package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CreateBookingDtoJsonValidationTest {

    private final JacksonTester<CreateBookingDto> json;

    private static Validator validator;
    private static LocalDateTime NOW;

    @BeforeAll
    static void initValidatorWithFixedClock() {
        // Фиксируем время: 2025-08-28T21:12:35Z (UTC)
        Clock fixedClock = Clock.fixed(Instant.parse("2025-08-28T21:12:35Z"), ZoneId.of("UTC"));
        ValidatorFactory factory = Validation.byDefaultProvider()
                .configure()
                .clockProvider(() -> fixedClock)
                .buildValidatorFactory();
        validator = factory.getValidator();
        NOW = LocalDateTime.now(fixedClock);
    }

    @Test
    void validJson_shouldPassValidation() throws Exception {
        String body = "{"
                + "\"start\":\"" + NOW.plusMinutes(1) + "\","
                + "\"end\":\"" + NOW.plusMinutes(2) + "\","
                + "\"itemId\":7"
                + "}";

        CreateBookingDto dto = json.parseObject(body);
        Set<ConstraintViolation<CreateBookingDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty(), "Не должно быть ошибок валидации");
    }

    @Test
    void missingStart_shouldFailNotNull() throws Exception {
        String body = "{"
                + "\"end\":\"" + NOW.plusMinutes(2) + "\","
                + "\"itemId\":7"
                + "}";

        CreateBookingDto dto = json.parseObject(body);
        Set<ConstraintViolation<CreateBookingDto>> violations = validator.validate(dto);

        assertHasViolation(violations, "start", "Дата начала бронирования не может быть пустой");
    }

    @Test
    void startNow_shouldFailFuture() throws Exception {
        String body = "{"
                + "\"start\":\"" + NOW + "\","
                + "\"end\":\"" + NOW.plusMinutes(2) + "\","
                + "\"itemId\":7"
                + "}";

        CreateBookingDto dto = json.parseObject(body);
        Set<ConstraintViolation<CreateBookingDto>> violations = validator.validate(dto);

        assertHasViolation(violations, "start", "Дата начала бронирования должна быть в будущем");
    }

    @Test
    void startInPast_shouldFailFuture() throws Exception {
        String body = "{"
                + "\"start\":\"" + NOW.minusSeconds(1) + "\","
                + "\"end\":\"" + NOW.plusMinutes(2) + "\","
                + "\"itemId\":7"
                + "}";

        CreateBookingDto dto = json.parseObject(body);
        Set<ConstraintViolation<CreateBookingDto>> violations = validator.validate(dto);

        assertHasViolation(violations, "start", "Дата начала бронирования должна быть в будущем");
    }

    @Test
    void missingEnd_shouldFailNotNull() throws Exception {
        String body = "{"
                + "\"start\":\"" + NOW.plusMinutes(1) + "\","
                + "\"itemId\":7"
                + "}";
        CreateBookingDto dto = json.parseObject(body);
        Set<ConstraintViolation<CreateBookingDto>> violations = validator.validate(dto);
        assertHasViolation(violations, "end", "Дата окончания бронирования не может быть пустой");
    }

    @Test
    void endNow_shouldFailFuture() throws Exception {
        String body = "{"
                + "\"start\":\"" + NOW.plusMinutes(1) + "\","
                + "\"end\":\"" + NOW + "\","
                + "\"itemId\":7"
                + "}";
        CreateBookingDto dto = json.parseObject(body);
        Set<ConstraintViolation<CreateBookingDto>> violations = validator.validate(dto);
        assertHasViolation(violations, "end", "Дата окончания бронирования должна быть в будущем");
    }

    @Test
    void endInPast_shouldFailFuture() throws Exception {
        String body = "{"
                + "\"start\":\"" + NOW.plusMinutes(1) + "\","
                + "\"end\":\"" + NOW.minusSeconds(1) + "\","
                + "\"itemId\":7"
                + "}";
        CreateBookingDto dto = json.parseObject(body);
        Set<ConstraintViolation<CreateBookingDto>> violations = validator.validate(dto);
        assertHasViolation(violations, "end", "Дата окончания бронирования должна быть в будущем");
    }

    @Test
    void itemIdNull_shouldFailNotNull() throws Exception {
        String body = "{"
                + "\"start\":\"" + NOW.plusMinutes(1) + "\","
                + "\"end\":\"" + NOW.plusMinutes(2) + "\","
                + "\"itemId\":null"
                + "}";
        CreateBookingDto dto = json.parseObject(body);
        Set<ConstraintViolation<CreateBookingDto>> violations = validator.validate(dto);
        assertHasViolation(violations, "itemId", "itemId не может быть null");
    }

    @Test
    void missingItemId_shouldFailNotNull() throws Exception {
        String body = "{"
                + "\"start\":\"" + NOW.plusMinutes(1) + "\","
                + "\"end\":\"" + NOW.plusMinutes(2) + "\""
                + "}";
        CreateBookingDto dto = json.parseObject(body);
        Set<ConstraintViolation<CreateBookingDto>> violations = validator.validate(dto);
        assertHasViolation(violations, "itemId", "itemId не может быть null");
    }

    private static void assertHasViolation(Set<ConstraintViolation<CreateBookingDto>> violations,
                                           String field, String message) {
        boolean found = violations.stream()
                .anyMatch(v -> field.equals(v.getPropertyPath().toString())
                        && message.equals(v.getMessage()));
        if (!found) {
            String all = violations.stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .reduce((a, b) -> a + "; " + b).orElse("<empty>");
            fail("Ожидали ошибку " + field + " -> '" + message + "'. Фактически: " + all);
        }
    }
}