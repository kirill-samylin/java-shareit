package ru.practicum.shareit.request.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequest {

    private Long id;

    private String description;

    private User requestor;

    private LocalDateTime created;
}