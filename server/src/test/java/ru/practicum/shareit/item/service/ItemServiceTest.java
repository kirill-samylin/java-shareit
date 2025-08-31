package ru.practicum.shareit.item.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceTest {

    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;

    private UserDto ownerUser;
    private UserDto bookerUser;

    @BeforeEach
    void setUp() {
        ownerUser = userService.createUser(new UserDto(null, "owner", "owner@ya.ru"));
        bookerUser = userService.createUser(new UserDto(null, "booker", "booker@ya.ru"));
    }

    @Test
    void addItemTest() {
        CreateItemDto create = new CreateItemDto("Drill", "Power drill", true, null);

        ItemDto saved = itemService.addItem(ownerUser.getId(), create);

        assertThat(saved.getId(), notNullValue());
        assertThat(saved.getName(), equalTo("Drill"));
        assertThat(saved.getDescription(), equalTo("Power drill"));
        assertThat(saved.getAvailable(), equalTo(true));
    }

    @Test
    void updateItemTest() {
        ItemDto created = itemService.addItem(ownerUser.getId(),
                new CreateItemDto("Drill", "Power drill", true, null));

        ItemDto patch = new ItemDto();
        patch.setName("Drill v2");
        patch.setDescription("Cordless power drill");
        patch.setAvailable(false);

        ItemDto updated = itemService.updateItem(ownerUser.getId(), created.getId(), patch);

        assertThat(updated.getId(), equalTo(created.getId()));
        assertThat(updated.getName(), equalTo("Drill v2"));
        assertThat(updated.getDescription(), equalTo("Cordless power drill"));
        assertThat(updated.getAvailable(), equalTo(false));
    }

    @Test
    void getItemByIdTest() {
        ItemDto created = itemService.addItem(ownerUser.getId(),
                new CreateItemDto("Hammer", "Steel hammer", true, null));

        ItemDto found = itemService.getItemById(ownerUser.getId(), created.getId());

        assertThat(found.getId(), equalTo(created.getId()));
        assertThat(found.getName(), equalTo("Hammer"));
        assertThat(found.getDescription(), equalTo("Steel hammer"));
    }

    @Test
    void getAllItemsByUserTest() {
        ItemDto i1 = itemService.addItem(ownerUser.getId(),
                new CreateItemDto("Drill", "Power drill", true, null));
        ItemDto i2 = itemService.addItem(ownerUser.getId(),
                new CreateItemDto("Hammer", "Steel hammer", true, null));

        List<ItemDto> items = itemService.getAllItemsByUser(ownerUser.getId());

        assertThat(items.size(), equalTo(2));
        // Проверим, что обе вещи есть в списке
        List<String> names = items.stream().map(ItemDto::getName).toList();
        assertThat(names, org.hamcrest.Matchers.containsInAnyOrder("Drill", "Hammer"));
    }

    @Test
    void searchItemsTest() {
        itemService.addItem(ownerUser.getId(),
                new CreateItemDto("Cordless Drill", "Good tool", true, null));
        itemService.addItem(ownerUser.getId(),
                new CreateItemDto("Hammer", "Steel hammer", true, null));

        List<ItemDto> found = itemService.searchItems("drill");

        assertThat(found.size(), equalTo(1));
        assertThat(found.getFirst().getName().toLowerCase(), equalTo("cordless drill"));
    }

    @Test
    void addCommentTest() {
        // 1) Владелец создает вещь
        ItemDto item = itemService.addItem(ownerUser.getId(),
                new CreateItemDto("Drill", "Power drill", true, null));
        // 2) Бронирующий создает бронирование в прошлом и владелец его подтверждает
        LocalDateTime start = LocalDateTime.now().minusHours(2);
        LocalDateTime end = LocalDateTime.now().minusHours(1);
        CreateBookingDto bookingCreate = new CreateBookingDto();
        bookingCreate.setItemId(item.getId());
        bookingCreate.setStart(start);
        bookingCreate.setEnd(end);
        BookingDto booking = bookingService.createBooking(bookerUser.getId(), bookingCreate);
        bookingService.approveBooking(booking.getId(), ownerUser.getId(), true);
        // 3) Бронирующий оставляет комментарий
        CreateCommentDto commentCreate = new CreateCommentDto("Great tool!");
        CommentDto comment = itemService.addComment(bookerUser.getId(), item.getId(), commentCreate);
        assertThat(comment.getId(), notNullValue());
        assertThat(comment.getText(), equalTo("Great tool!"));
        // Если в CommentDto есть authorName — можно проверить автора
        // assertThat(comment.getAuthorName(), equalTo(bookerUser.getName()));
    }
}
