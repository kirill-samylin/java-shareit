package ru.practicum.shareit.exception;

public class BookingNotFoundException extends RuntimeException {
    public BookingNotFoundException(Long itemId) {
        super("Бронирование с id = " + itemId + " не найден");
    }
}
