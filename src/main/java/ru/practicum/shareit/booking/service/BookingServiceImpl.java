package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;


    @Override
    public BookingDto createBooking(Long userId, CreateBookingDto bookingDto) {

        if (bookingDto.getStart().isEqual(bookingDto.getEnd())) {
            throw new IllegalArgumentException("Дата начала и окончания бронирования не могут совпадать");
        }

        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new IllegalArgumentException("Дата окончания не может быть раньше даты начала");
        }

        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new ItemNotFoundException(bookingDto.getItemId()));

        if (Objects.equals(item.getOwner().getId(), userId)) {
            throw new IllegalArgumentException("Владелец не может бронировать собственную вещь");
        }

        if (!item.getAvailable()) {
            throw new IllegalStateException("Предмет недоступен для бронирования");
        }

        Booking booking = BookingMapper.toEntity(bookingDto);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        return BookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto approveBooking(Long bookingId, Long ownerId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new AccessDeniedException("Только владелец может подтверждать бронирование");
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new IllegalStateException("Бронирование уже рассмотрено");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return BookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        Long bookerId = booking.getBooker().getId();
        Long ownerId = booking.getItem().getOwner().getId();

        if (!bookerId.equals(userId) && !ownerId.equals(userId)) {
            throw new IllegalArgumentException("Нет доступа к бронированию");
        }

        return BookingMapper.toDto(booking);
    }

    @Override
    public List<BookingDto> getBookingsByBooker(Long userId, String state) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        List<Booking> bookings = filterBookings(
                bookingRepository.findByBookerOrderByStartDesc(booker),
                state
        );
        return bookings.stream().map(BookingMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getBookingsByOwner(Long userId, String state) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        List<Booking> bookings = filterBookings(
                bookingRepository.findByItemOwnerOrderByStartDesc(owner),
                state
        );
        return bookings.stream().map(BookingMapper::toDto).collect(Collectors.toList());
    }

    // Фильтрация по state
    private List<Booking> filterBookings(List<Booking> bookings, String state) {
        LocalDateTime now = LocalDateTime.now();
        return bookings.stream().filter(booking -> switch (state.toUpperCase()) {
            case "ALL" -> true;
            case "CURRENT" -> now.isAfter(booking.getStart()) && now.isBefore(booking.getEnd());
            case "PAST" -> now.isAfter(booking.getEnd());
            case "FUTURE" -> now.isBefore(booking.getStart());
            case "WAITING" -> booking.getStatus() == BookingStatus.WAITING;
            case "REJECTED" -> booking.getStatus() == BookingStatus.REJECTED;
            default -> throw new IllegalArgumentException("Unknown state: " + state);
        }).collect(Collectors.toList());
    }

    @Override
    public void deleteBooking(Long bookingId) {
        if (!bookingRepository.existsById(bookingId)) {
            throw new BookingNotFoundException(bookingId);
        }
        bookingRepository.deleteById(bookingId);
    }
}