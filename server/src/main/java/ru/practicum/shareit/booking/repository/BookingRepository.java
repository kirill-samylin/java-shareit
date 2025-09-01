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

    // Все бронирования по владельцу вещи
    List<Booking> findByItemOwnerOrderByStartDesc(User owner);

    boolean existsByItem_IdAndBooker_IdAndEndBefore(Long itemId, Long userId, LocalDateTime end);

    // Пересечение при полуоткрытом интервале [start, end)
    boolean existsByItem_IdAndStatusAndStartBeforeAndEndAfter(
            Long itemId,
            BookingStatus status,
            LocalDateTime requestedEnd,
            LocalDateTime requestedStart
    );
}