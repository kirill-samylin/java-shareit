package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Все бронирования, сделанные пользователем
    List<Booking> findByBookerOrderByStartDesc(User booker);

    // Все бронирования с определённым статусом
    List<Booking> findByBookerAndStatusOrderByStartDesc(User booker, BookingStatus status);

    // CURRENT: сейчас происходит
    List<Booking> findByBookerAndStartBeforeAndEndAfterOrderByStartDesc(User booker, LocalDateTime now1, LocalDateTime now2);

    // PAST: закончились
    List<Booking> findByBookerAndEndBeforeOrderByStartDesc(User booker, LocalDateTime now);

    // FUTURE: еще не начались
    List<Booking> findByBookerAndStartAfterOrderByStartDesc(User booker, LocalDateTime now);

    // Все бронирования по владельцу вещи
    List<Booking> findByItemOwnerOrderByStartDesc(User owner);

    // По владельцу и статусу
    List<Booking> findByItemOwnerAndStatusOrderByStartDesc(User owner, BookingStatus status);

    // CURRENT: сейчас происходит (по owner)
    List<Booking> findByItemOwnerAndStartBeforeAndEndAfterOrderByStartDesc(User owner, LocalDateTime now1, LocalDateTime now2);

    // PAST: закончились (по owner)
    List<Booking> findByItemOwnerAndEndBeforeOrderByStartDesc(User owner, LocalDateTime now);

    // FUTURE: еще не начались (по owner)
    List<Booking> findByItemOwnerAndStartAfterOrderByStartDesc(User owner, LocalDateTime now);

    boolean existsByItem_IdAndBooker_IdAndEndBefore(Long itemId, Long userId, LocalDateTime end);

}