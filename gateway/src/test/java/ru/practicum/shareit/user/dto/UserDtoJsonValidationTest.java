package ru.practicum.shareit.user.dto;

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
class UserDtoJsonValidationTest {

    private final JacksonTester<UserDto> json;

    private static Validator validator;

    @BeforeAll
    static void initValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validJson_shouldPassValidation() throws Exception {
        String body = "{\"id\":1,\"name\":\"Иван\",\"email\":\"ivan@example.com\"}";

        UserDto dto = json.parseObject(body);
        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty(), "Должно не быть ошибок валидации");
    }

    @Test
    void missingName_shouldFailNotBlank() throws Exception {
        String body = "{\"email\":\"ivan@example.com\"}";

        UserDto dto = json.parseObject(body);
        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);

        assertHasViolation(violations, "name", "Поле 'name' обязательно для заполнения");
    }

    @Test
    void blankName_shouldFailNotBlank() throws Exception {
        String body = "{\"name\":\"   \",\"email\":\"ivan@example.com\"}";

        UserDto dto = json.parseObject(body);
        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);

        assertHasViolation(violations, "name", "Поле 'name' обязательно для заполнения");
    }

    @Test
    void missingEmail_shouldFailNotBlank() throws Exception {
        String body = "{\"name\":\"Иван\"}";

        UserDto dto = json.parseObject(body);
        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);

        assertHasViolation(violations, "email", "Поле 'email' обязательно для заполнения");
    }

    @Test
    void blankEmail_shouldFailNotBlank() throws Exception {
        String body = "{\"name\":\"Иван\",\"email\":\"   \"}";

        UserDto dto = json.parseObject(body);
        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);

        assertHasViolation(violations, "email", "Поле 'email' обязательно для заполнения");
    }

    @Test
    void badEmailFormat_shouldFailEmail() throws Exception {
        String body = "{\"name\":\"Иван\",\"email\":\"bad-email\"}";

        UserDto dto = json.parseObject(body);
        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);

        assertHasViolation(violations, "email", "Некорректный формат email");
    }

    private static void assertHasViolation(Set<ConstraintViolation<UserDto>> violations,
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