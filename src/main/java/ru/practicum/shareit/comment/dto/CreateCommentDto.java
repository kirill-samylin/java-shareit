package ru.practicum.shareit.comment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCommentDto {

    @NotBlank(message = "Комментарий не должен быть пустым")
    private String text;
}
