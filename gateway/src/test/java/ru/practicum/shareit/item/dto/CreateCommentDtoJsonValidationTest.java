package ru.practicum.shareit.item.dto;

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

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CreateCommentDtoJsonValidationTest {

    private final JacksonTester<CreateCommentDto> json;

    private static Validator validator;

    @BeforeAll
    static void initValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validJson_shouldPassValidation() throws Exception {
        String body = "{\"text\":\"Отличная вещь!\"}";

        CreateCommentDto dto = json.parseObject(body);
        Set<ConstraintViolation<CreateCommentDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty(), "Не должно быть ошибок валидации");
    }

    @Test
    void missingText_shouldFailNotBlank() throws Exception {
        String body = "{}";

        CreateCommentDto dto = json.parseObject(body);
        Set<ConstraintViolation<CreateCommentDto>> violations = validator.validate(dto);

        assertHasViolation(violations, "text", "Комментарий не должен быть пустым");
    }

    @Test
    void blankText_shouldFailNotBlank() throws Exception {
        String body = "{\"text\":\"   \"}";

        CreateCommentDto dto = json.parseObject(body);
        Set<ConstraintViolation<CreateCommentDto>> violations = validator.validate(dto);

        assertHasViolation(violations, "text", "Комментарий не должен быть пустым");
    }

    @Test
    void emptyText_shouldFailNotBlank() throws Exception {
        String body = "{\"text\":\"\"}";

        CreateCommentDto dto = json.parseObject(body);
        Set<ConstraintViolation<CreateCommentDto>> violations = validator.validate(dto);

        assertHasViolation(violations, "text", "Комментарий не должен быть пустым");
    }

    private static void assertHasViolation(Set<ConstraintViolation<CreateCommentDto>> violations,
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
