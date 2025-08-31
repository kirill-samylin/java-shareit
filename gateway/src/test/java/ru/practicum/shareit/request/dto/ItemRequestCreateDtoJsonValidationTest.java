package ru.practicum.shareit.request.dto;

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
public class ItemRequestCreateDtoJsonValidationTest {
    private final JacksonTester<ItemRequestCreateDto> json;

    private static Validator validator;

    @BeforeAll
    static void initValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validJson_shouldPassValidation() throws Exception {
        String body = """
                {
                  "id": 10,
                  "description": "Нужен молоток"
                }
                """;

        ItemRequestCreateDto dto = json.parseObject(body);
        Set<ConstraintViolation<ItemRequestCreateDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty(), "Не должно быть ошибок валидации");
    }

    @Test
    void missingDescription_shouldFailNotBlank() throws Exception {
        String body = """
                {
                  "id": 10
                }
                """;

        ItemRequestCreateDto dto = json.parseObject(body);
        Set<ConstraintViolation<ItemRequestCreateDto>> violations = validator.validate(dto);

        assertHasViolation(violations, "description", "Описание не должно быть пустым");
    }

    @Test
    void blankDescription_shouldFailNotBlank() throws Exception {
        String body = """
                {
                  "description": "   "
                }
                """;

        ItemRequestCreateDto dto = json.parseObject(body);
        Set<ConstraintViolation<ItemRequestCreateDto>> violations = validator.validate(dto);

        assertHasViolation(violations, "description", "Описание не должно быть пустым");
    }

    @Test
    void emptyDescription_shouldFailNotBlank() throws Exception {
        String body = """
                {
                  "description": ""
                }
                """;

        ItemRequestCreateDto dto = json.parseObject(body);
        Set<ConstraintViolation<ItemRequestCreateDto>> violations = validator.validate(dto);

        assertHasViolation(violations, "description", "Описание не должно быть пустым");
    }

    private static void assertHasViolation(Set<ConstraintViolation<ItemRequestCreateDto>> violations,
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
