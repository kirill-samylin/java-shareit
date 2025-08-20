package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;

import java.util.List;

public interface BookingService {

    BookingDto createBooking(Long userId, CreateBookingDto bookingDto);

    BookingDto approveBooking(Long bookingId, Long ownerId, boolean approved);

    BookingDto getBookingById(Long bookingId, Long userId);

    List<BookingDto> getBookingsByBooker(Long userId, String state);

    List<BookingDto> getBookingsByOwner(Long userId, String state);

    void deleteBooking(Long bookingId);
}
