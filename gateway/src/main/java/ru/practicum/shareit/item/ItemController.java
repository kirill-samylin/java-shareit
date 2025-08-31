package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collections;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    // Добавление новой вещи
    @PostMapping
    public ResponseEntity<Object> addItem(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestBody @Valid CreateItemDto itemDto
    ) {
        log.info("Create item {}, userId={}", itemDto, userId);
        return itemClient.addItem(userId, itemDto);
    }

    // Редактирование вещи (partial update)
    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable Long itemId,
            @RequestBody ItemDto itemDto
    ) {
        log.info("Update item {}, userId={}, body={}", itemId, userId, itemDto);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    // Получение вещи по ID
    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable Long itemId
    ) {
        log.info("Get item {}, userId={}", itemId, userId);
        return itemClient.getItemById(userId, itemId);
    }

    // Список всех вещей владельца (с пагинацией)
    @GetMapping
    public ResponseEntity<Object> getAllItemsByUser(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size
    ) {
        log.info("Get items of userId={}, from={}, size={}", userId, from, size);
        return itemClient.getAllItemsByUser(userId, from, size);
    }

    // Поиск вещей по тексту (с пагинацией)
    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam String text,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size
    ) {
        log.info("Search items by text='{}', userId={}, from={}, size={}", text, userId, from, size);
        if (text == null || text.isBlank()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        return itemClient.searchItems(userId, text, from, size);
    }

    // Оставить комментарий
    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable Long itemId,
            @RequestBody @Valid CreateCommentDto body
    ) {
        log.info("Add comment to item {}, userId={}, body={}", itemId, userId, body);
        return itemClient.addComment(userId, itemId, body);
    }
}