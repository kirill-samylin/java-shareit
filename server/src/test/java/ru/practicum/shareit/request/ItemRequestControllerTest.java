package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    @Autowired
    ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    ItemRequestService service;

    private final UserDto requester = new UserDto(2L, "requester", "requester@requester.com");
    private final ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "description", requester, LocalDateTime.now(), null);

    @Test
    void createItemRequestTest() throws Exception {
        ItemRequestCreateDto itemRequestCreateDto = new ItemRequestCreateDto("description");
        when(service.createRequest(eq(2L), any(ItemRequestCreateDto.class))).thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                .content(mapper.writeValueAsString(itemRequestCreateDto))
                .header("X-Sharer-User-Id", 2L)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.description").value(itemRequestDto.getDescription()));

        verify(service).createRequest(eq(2L), any(ItemRequestCreateDto.class));
    }

    @Test
    void getOwnRequestsTest() throws Exception {
        List<ItemRequestDto> list = List.of(itemRequestDto);

        when(service.getOwnRequests(eq(2L))).thenReturn(list);

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].description").value(itemRequestDto.getDescription()));

        verify(service).getOwnRequests(2L);
    }

    @Test
    void getAllRequestsTest() throws Exception {
        List<ItemRequestDto> list = List.of(itemRequestDto);
        when(service.getAllRequests(eq(2L))).thenReturn(list);

        mvc.perform(get("/requests/all")
                .header("X-Sharer-User-Id", 2L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].description").value(itemRequestDto.getDescription()));

        verify(service).getAllRequests(2L);
    }

    @Test
    void getRequestByIdTest() throws Exception {
        when(service.getRequestById(eq(2L), eq(1L))).thenReturn(itemRequestDto);

        mvc.perform(get("/requests/{requestId}", 1L)
                .header("X-Sharer-User-Id", 2L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(itemRequestDto.getId()))
            .andExpect(jsonPath("$.description").value(itemRequestDto.getDescription()));

        verify(service).getRequestById(2L, 1L);
    }
}