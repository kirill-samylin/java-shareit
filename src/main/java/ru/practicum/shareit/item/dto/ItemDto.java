package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemDto {

    private Long id;

    @NotBlank(message = "Название не должно быть пустым")
    private String name;

    @NotBlank(message = "Описание не должно быть пустым")
    private String description;

    @NotNull(message = "Поле 'available' обязательно для заполнения")
    private Boolean available;

    private ItemRequestDto request;

    private List<CommentDto> comments;

    private LocalDateTime lastBooking;

    private LocalDateTime nextBooking;
}