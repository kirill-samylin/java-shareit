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
class CreateItemDtoJsonValidationTest {

    private final JacksonTester<CreateItemDto> json;

    private static Validator validator;

    @BeforeAll
    static void initValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validJson_shouldPassValidation() throws Exception {
        String body = "{\"id\":5,\"name\":\"Дрель\",\"description\":\"Ударная дрель\",\"available\":true,\"requestId\":42}";

        CreateItemDto dto = json.parseObject(body);
        Set<ConstraintViolation<CreateItemDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty(), "Не должно быть ошибок валидации");
    }

    @Test
    void validWithFalseAvailable_shouldPassValidation() throws Exception {
        String body = "{\"name\":\"Дрель\",\"description\":\"С обычным патроном\",\"available\":false}";

        CreateItemDto dto = json.parseObject(body);
        Set<ConstraintViolation<CreateItemDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty(), "available=false валиден");
    }

    @Test
    void missingName_shouldFailNotBlank() throws Exception {
        String body = "{\"description\":\"Описание\",\"available\":true}";

        CreateItemDto dto = json.parseObject(body);
        Set<ConstraintViolation<CreateItemDto>> violations = validator.validate(dto);

        assertHasViolation(violations, "name", "Название не должно быть пустым");
    }

    @Test
    void blankName_shouldFailNotBlank() throws Exception {
        String body = "{\"name\":\"   \",\"description\":\"Описание\",\"available\":true}";

        CreateItemDto dto = json.parseObject(body);
        Set<ConstraintViolation<CreateItemDto>> violations = validator.validate(dto);

        assertHasViolation(violations, "name", "Название не должно быть пустым");
    }

    @Test
    void missingDescription_shouldFailNotBlank() throws Exception {
        String body = "{\"name\":\"Дрель\",\"available\":true}";

        CreateItemDto dto = json.parseObject(body);
        Set<ConstraintViolation<CreateItemDto>> violations = validator.validate(dto);

        assertHasViolation(violations, "description", "Описание не должно быть пустым");
    }

    @Test
    void blankDescription_shouldFailNotBlank() throws Exception {
        String body = "{\"name\":\"Дрель\",\"description\":\"   \",\"available\":true}";

        CreateItemDto dto = json.parseObject(body);
        Set<ConstraintViolation<CreateItemDto>> violations = validator.validate(dto);

        assertHasViolation(violations, "description", "Описание не должно быть пустым");
    }

    @Test
    void missingAvailable_shouldFailNotNull() throws Exception {
        String body = "{\"name\":\"Дрель\",\"description\":\"Описание\"}";

        CreateItemDto dto = json.parseObject(body);
        Set<ConstraintViolation<CreateItemDto>> violations = validator.validate(dto);
        assertHasViolation(violations, "available", "Поле 'available' обязательно для заполнения");
    }

    @Test
    void nullAvailable_shouldFailNotNull() throws Exception {
        String body = "{\"name\":\"Дрель\",\"description\":\"Описание\",\"available\":null}";
        CreateItemDto dto = json.parseObject(body);
        Set<ConstraintViolation<CreateItemDto>> violations = validator.validate(dto);
        assertHasViolation(violations, "available", "Поле 'available' обязательно для заполнения");
    }

    private static void assertHasViolation(Set<ConstraintViolation<CreateItemDto>> violations,
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