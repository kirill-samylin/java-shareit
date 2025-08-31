package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ItemDto {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private ItemRequestDto request;

    private List<CommentDto> comments;

    private LocalDateTime lastBooking;

    private LocalDateTime nextBooking;

}