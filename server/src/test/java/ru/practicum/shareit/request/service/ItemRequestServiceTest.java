package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceTest {
    private final ItemRequestService service;
    private final UserService userService;
    private final ItemService itemService;
    private UserDto requestorUserDto;
    private UserDto otherUserDto;

    @BeforeEach
    void setUp() {
        UserDto userCreateDto = new UserDto(null, "user 1", "user1@ya.ru");
        this.requestorUserDto = userService.createUser(userCreateDto);
        UserDto otherUserCreateDto = new UserDto(null, "user 2", "user2@ya.ru");
        this.otherUserDto = userService.createUser(otherUserCreateDto);
    }

    private ItemRequestDto createRequest() {
        ItemRequestCreateDto itemRequestCreateDto = new ItemRequestCreateDto("description");
        return service.createRequest(otherUserDto.getId(), itemRequestCreateDto);
    }

    @Test
    void createRequestTest() {
        ItemRequestCreateDto itemRequestCreateDto = new ItemRequestCreateDto("description");

        ItemRequestDto itemRequestDto = service.createRequest(requestorUserDto.getId(), itemRequestCreateDto);

        assertThat(itemRequestDto.getId(), notNullValue());
        assertThat(itemRequestDto.getDescription(), equalTo(itemRequestCreateDto.getDescription()));
        assertThat(itemRequestDto.getRequestor().getId(), equalTo(requestorUserDto.getId()));
    }

    @Test
    void getAllRequestsTest() {
        ItemRequestDto itemRequestDto = createRequest();

        List<ItemRequestDto> list = service.getAllRequests(requestorUserDto.getId());
        List<ItemRequestDto> otherList = service.getAllRequests(otherUserDto.getId());

        assertThat(list.size(), equalTo(1));
        assertThat(list.getFirst().getId(), equalTo(itemRequestDto.getId()));
        assertThat(otherList.size(), equalTo(0));
    }

    @Test
    void getOwnRequestsTest() {
        ItemRequestDto itemRequestDto = createRequest();

        List<ItemRequestDto> otherList = service.getOwnRequests(otherUserDto.getId());
        List<ItemRequestDto> requestorList = service.getOwnRequests(requestorUserDto.getId());

        assertThat(otherList.size(), equalTo(1));
        assertThat(otherList.getFirst().getId(), equalTo(itemRequestDto.getId()));
        assertThat(requestorList.size(), equalTo(0));
    }

    @Test
    void getRequestByIdTest() {
        ItemRequestDto itemRequestDto = createRequest();
        CreateItemDto itemCreateDto = new CreateItemDto("Item", "Description", true, itemRequestDto.getId());
        ItemDto itemDto = itemService.addItem(otherUserDto.getId(), itemCreateDto);

        ItemRequestDto itemRequestWithItemsDto = service.getRequestById(otherUserDto.getId(), itemRequestDto.getId());

        assertThat(itemRequestWithItemsDto.getId(), equalTo(itemRequestDto.getId()));
        assertThat(itemRequestWithItemsDto.getItems().size(), equalTo(1));
        assertThat(itemRequestWithItemsDto.getItems().getFirst().getId(), equalTo(itemDto.getId()));
    }
}