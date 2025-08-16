package ru.practicum.shareit.exception;

public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException(Long itemId) {
        super("Предмет с id = " + itemId + " не найден");
    }
}
