package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ItemRequestCreateDto {

    private Long id;

    @NotBlank(message = "Описание не должно быть пустым")
    private String description;
}