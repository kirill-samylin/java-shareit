package ru.practicum.shareit.booking.service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class BookingServiceTest {

    @Autowired
    private BookingService bookingService;
    @Autowired
    private UserService userService;
    @Autowired
    private ItemService itemService;

    private UserDto owner;
    private UserDto booker;
    private ItemDto item;

    @BeforeEach
    void setUp() {
        owner = userService.createUser(new UserDto(null, "owner", "owner@ya.ru"));
        booker = userService.createUser(new UserDto(null, "booker", "booker@ya.ru"));

        item = itemService.addItem(owner.getId(),
                new CreateItemDto("Drill", "Power drill", true, null));
    }

    private CreateBookingDto newBookingDto(Long itemId, LocalDateTime start, LocalDateTime end) {
        CreateBookingDto dto = new CreateBookingDto();
        dto.setItemId(itemId);
        dto.setStart(start);
        dto.setEnd(end);
        return dto;
    }

    @Test
    void createBookingTest() {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(3);
        CreateBookingDto create = newBookingDto(item.getId(), start, end);

        BookingDto booking = bookingService.createBooking(booker.getId(), create);

        assertThat(booking.getId(), notNullValue());
        // сразу после создания бронь обычно WAITING
        assertThat(String.valueOf(booking.getStatus()), anyOf(is("WAITING"), is("WAITING")));
    }

    @Test
    void approveBookingTest() {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(2);
        BookingDto booking = bookingService.createBooking(booker.getId(),
                newBookingDto(item.getId(), start, end));

        BookingDto approved = bookingService.approveBooking(booking.getId(), owner.getId(), true);

        assertThat(approved.getId(), equalTo(booking.getId()));
        assertThat(String.valueOf(approved.getStatus()), is("APPROVED"));
    }

    @Test
    void getBookingByIdTest() {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(2);
        BookingDto booking = bookingService.createBooking(booker.getId(),
                newBookingDto(item.getId(), start, end));

        BookingDto asBooker = bookingService.getBookingById(booking.getId(), booker.getId());
        assertThat(asBooker.getId(), equalTo(booking.getId()));

        BookingDto asOwner = bookingService.getBookingById(booking.getId(), owner.getId());
        assertThat(asOwner.getId(), equalTo(booking.getId()));
    }

    @Test
    void getBookingsByBookerTest() {
        // Вторая вещь, чтобы сделать вторую бронь
        ItemDto item2 = itemService.addItem(owner.getId(),
                new CreateItemDto("Hammer", "Steel hammer", true, null));

        // 1) WAITING
        BookingDto b1 = bookingService.createBooking(booker.getId(),
                newBookingDto(item.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(2)));
        // 2) REJECTED
        BookingDto b2 = bookingService.createBooking(booker.getId(),
                newBookingDto(item2.getId(), LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(2).plusHours(2)));
        bookingService.approveBooking(b2.getId(), owner.getId(), false);
        List<BookingDto> all = bookingService.getBookingsByBooker(booker.getId(), "ALL");
        assertThat(all, hasSize(2));
        List<BookingDto> waiting = bookingService.getBookingsByBooker(booker.getId(), "WAITING");
        assertThat(waiting, hasSize(1));
        assertThat(String.valueOf(waiting.getFirst().getStatus()), is("WAITING"));
        List<BookingDto> rejected = bookingService.getBookingsByBooker(booker.getId(), "REJECTED");
        assertThat(rejected, hasSize(1));
        assertThat(String.valueOf(rejected.getFirst().getStatus()), is("REJECTED"));
    }

    @Test
    void getBookingsByOwnerTest() {
        // Две брони на вещи владельца
        BookingDto b1 = bookingService.createBooking(booker.getId(),
                newBookingDto(item.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(2)));
        BookingDto b2 = bookingService.createBooking(booker.getId(),
                newBookingDto(item.getId(), LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(2).plusHours(2)));

        bookingService.approveBooking(b2.getId(), owner.getId(), true);

        List<BookingDto> all = bookingService.getBookingsByOwner(owner.getId(), "ALL");
        assertThat(all.size(), equalTo(2));
        List<BookingDto> approved = bookingService.getBookingsByOwner(owner.getId(), "APPROVED");
        assertThat(approved, hasSize(1));
        assertThat(String.valueOf(approved.getFirst().getStatus()), is("APPROVED"));
    }

    @Test
    void deleteBookingTest() {
        BookingDto booking = bookingService.createBooking(booker.getId(),
                newBookingDto(item.getId(),
                        LocalDateTime.now().plusDays(1),
                        LocalDateTime.now().plusDays(1).plusHours(2)));
        Long bookingId = booking.getId();

        bookingService.deleteBooking(bookingId);

        assertThatThrownBy(() -> bookingService.getBookingById(bookingId, owner.getId()))
                .isInstanceOf(BookingNotFoundException.class)
                .hasMessageContaining(String.valueOf(bookingId));
    }

}