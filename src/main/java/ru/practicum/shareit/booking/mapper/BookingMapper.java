package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {

    // Преобразование Booking → BookingDto
    public static BookingDto toDto(Booking booking) {
        if (booking == null) {
            return null;
        }

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());

        // Предполагается, что ItemMapper и User должны быть адаптированы под требования
        bookingDto.setItem(ItemMapper.toDto(booking.getItem()));
        bookingDto.setBooker(UserMapper.toDto(booking.getBooker()));
        bookingDto.setStatus(booking.getStatus());

        return bookingDto;
    }

    // Преобразование BookingDto → Booking
    public static Booking toEntity(CreateBookingDto dto, Item item, User booker) {
        if (dto == null) {
            return null;
        }

        Booking booking = new Booking();
        booking.setId(dto.getId());
        booking.setStart(dto.getStart());
        booking.setEnd(dto.getEnd());

        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(dto.getStatus());

        return booking;
    }
}