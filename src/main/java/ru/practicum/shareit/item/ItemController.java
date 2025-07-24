package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    // Добавление новой вещи
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto addItem(
        @RequestHeader("X-Sharer-User-Id") Long userId,
        @RequestBody @Valid ItemDto itemDto
    ) {
        return itemService.addItem(userId, itemDto);
    }

    // Редактирование вещи
    @PatchMapping("/{itemId}")
    public ItemDto updateItem(
        @RequestHeader("X-Sharer-User-Id") Long userId,
        @PathVariable Long itemId,
        @RequestBody ItemDto itemDto
    ) {
        return itemService.updateItem(userId, itemId, itemDto);
    }

    // Получение вещи по ID
    @GetMapping("/{itemId}")
    public ItemDto getItemById(
        @RequestHeader("X-Sharer-User-Id") Long userId,
        @PathVariable Long itemId
    ) {
        return itemService.getItemById(userId, itemId);
    }

    // Список всех вещей владельца
    @GetMapping
    public List<ItemDto> getAllItemsByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getAllItemsByUser(userId);
    }

    // Поиск вещей по тексту
    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        return itemService.searchItems(text);
    }
}