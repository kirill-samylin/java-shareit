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
class ItemDtoJsonValidationTest {

    private final JacksonTester<ItemDto> json;

    private static Validator validator;

    @BeforeAll
    static void initValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validJson_shouldPassValidation() throws Exception {
        String body = "{\"id\":7,\"name\":\"Перфоратор\",\"description\":\"Мощный перфоратор\",\"available\":true}";

        ItemDto dto = json.parseObject(body);
        Set<ConstraintViolation<ItemDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty(), "Не должно быть ошибок валидации");
    }

    @Test
    void validFullJson_shouldPassValidation() throws Exception {
        String body = "{\"id\":8,\"name\":\"Шуруповерт\",\"description\":\"С двумя батареями\",\"available\":false,"
                + "\"lastBooking\":\"2025-08-28T21:12:35\",\"nextBooking\":\"2025-08-29T09:00:00\","
                + "\"request\":{\"id\":123},"
                + "\"comments\":[{\"id\":1,\"text\":\"Норм\"}]}";

        ItemDto dto = json.parseObject(body);
        Set<ConstraintViolation<ItemDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty(), "Полный корректный JSON должен проходить валидацию");
    }

    @Test
    void missingName_shouldFailNotBlank() throws Exception {
        String body = "{\"description\":\"Описание\",\"available\":true}";

        ItemDto dto = json.parseObject(body);
        Set<ConstraintViolation<ItemDto>> violations = validator.validate(dto);

        assertHasViolation(violations, "name", "Название не должно быть пустым");
    }

    @Test
    void blankName_shouldFailNotBlank() throws Exception {
        String body = "{\"name\":\"   \",\"description\":\"Описание\",\"available\":true}";

        ItemDto dto = json.parseObject(body);
        Set<ConstraintViolation<ItemDto>> violations = validator.validate(dto);

        assertHasViolation(violations, "name", "Название не должно быть пустым");
    }

    @Test
    void missingDescription_shouldFailNotBlank() throws Exception {
        String body = "{\"name\":\"Перфоратор\",\"available\":true}";

        ItemDto dto = json.parseObject(body);
        Set<ConstraintViolation<ItemDto>> violations = validator.validate(dto);

        assertHasViolation(violations, "description", "Описание не должно быть пустым");
    }

    @Test
    void blankDescription_shouldFailNotBlank() throws Exception {
        String body = "{\"name\":\"Перфоратор\",\"description\":\"   \",\"available\":true}";

        ItemDto dto = json.parseObject(body);
        Set<ConstraintViolation<ItemDto>> violations = validator.validate(dto);

        assertHasViolation(violations, "description", "Описание не должно быть пустым");
    }

    @Test
    void missingAvailable_shouldFailNotNull() throws Exception {
        String body = "{\"name\":\"Перфоратор\",\"description\":\"Описание\"}";
        ItemDto dto = json.parseObject(body);
        Set<ConstraintViolation<ItemDto>> violations = validator.validate(dto);
        assertHasViolation(violations, "available", "Поле 'available' обязательно для заполнения");
    }

    @Test
    void nullAvailable_shouldFailNotNull() throws Exception {
        String body = "{\"name\":\"Перфоратор\",\"description\":\"Описание\",\"available\":null}";
        ItemDto dto = json.parseObject(body);
        Set<ConstraintViolation<ItemDto>> violations = validator.validate(dto);
        assertHasViolation(violations, "available", "Поле 'available' обязательно для заполнения");
    }

    private static void assertHasViolation(Set<ConstraintViolation<ItemDto>> violations,
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