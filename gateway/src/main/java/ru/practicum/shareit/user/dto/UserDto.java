package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserDto {
    private Long id;

    @NotBlank(message = "Поле 'name' обязательно для заполнения")
    private String name;

    @NotBlank(message = "Поле 'email' обязательно для заполнения")
    @Email(message = "Некорректный формат email")
    private String email;
}