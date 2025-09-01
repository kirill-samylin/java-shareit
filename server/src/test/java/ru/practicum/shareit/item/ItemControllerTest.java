package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ItemService itemService;

    private ItemDto itemDto;
    private CreateItemDto createItemDto;
    private CommentDto commentDto;
    private CreateCommentDto createCommentDto;

    @BeforeEach
    void setUp() {
        createItemDto = new CreateItemDto("Drill", "Power drill", true, null);

        itemDto = new ItemDto(1L, "Drill", "Power drill", true, null, null, null, null);

        createCommentDto = new CreateCommentDto("Great tool!");

        commentDto = new CommentDto(10L, "Great tool!", "Alice", LocalDateTime.now());
    }

    @Test
    void addItemTest() throws Exception {
        when(itemService.addItem(eq(2L), any(CreateItemDto.class))).thenReturn(itemDto);

        mvc.perform(post("/items")
                .header("X-Sharer-User-Id", 2L)
                .content(mapper.writeValueAsString(createItemDto))
                .characterEncoding(java.nio.charset.StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Drill"))
            .andExpect(jsonPath("$.available").value(true));

        verify(itemService).addItem(eq(2L), any(CreateItemDto.class));
    }

    @Test
    void updateItemTest() throws Exception {
        ItemDto patch = new ItemDto();
        patch.setDescription("Cordless power drill");

        ItemDto updated = new ItemDto();
        updated.setId(1L);
        updated.setName("Drill");
        updated.setDescription("Cordless power drill");
        updated.setAvailable(true);

        when(itemService.updateItem(eq(2L), eq(1L), any(ItemDto.class))).thenReturn(updated);

        mvc.perform(patch("/items/{itemId}", 1L)
                .header("X-Sharer-User-Id", 2L)
                .content(mapper.writeValueAsString(patch))
                .characterEncoding(java.nio.charset.StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.description").value("Cordless power drill"));

        verify(itemService).updateItem(eq(2L), eq(1L), any(ItemDto.class));
    }

    @Test
    void getItemByIdTest() throws Exception {
        when(itemService.getItemById(eq(2L), eq(1L))).thenReturn(itemDto);

        mvc.perform(get("/items/{itemId}", 1L)
                .header("X-Sharer-User-Id", 2L)
                .characterEncoding(java.nio.charset.StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Drill"));

        verify(itemService).getItemById(2L, 1L);
    }

    @Test
    void getAllItemsByUserTest() throws Exception {
        ItemDto item2 = new ItemDto();
        item2.setId(2L);
        item2.setName("Hammer");
        item2.setDescription("Steel hammer");
        item2.setAvailable(true);
        when(itemService.getAllItemsByUser(eq(2L)))
            .thenReturn(java.util.List.of(itemDto, item2));
        mvc.perform(get("/items")
                .header("X-Sharer-User-Id", 2L)
                .characterEncoding(java.nio.charset.StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(2)))
            .andExpect(jsonPath("$[0].name").value("Drill"))
            .andExpect(jsonPath("$[1].name").value("Hammer"));
        verify(itemService).getAllItemsByUser(2L);
    }

    @Test
    void searchItemsTest() throws Exception {
        when(itemService.searchItems(eq("drill")))
            .thenReturn(java.util.List.of(itemDto));
        mvc.perform(get("/items/search")
                .param("text", "drill")
                .characterEncoding(java.nio.charset.StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(1)))
            .andExpect(jsonPath("$[0].name").value("Drill"));
        verify(itemService).searchItems("drill");
    }

    @Test
    void addCommentTest() throws Exception {
        when(itemService.addComment(eq(2L), eq(1L), any(CreateCommentDto.class)))
            .thenReturn(commentDto);
        mvc.perform(post("/items/{itemId}/comment", 1L)
                .header("X-Sharer-User-Id", 2L)
                .content(mapper.writeValueAsString(createCommentDto))
                .characterEncoding(java.nio.charset.StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(10))
            .andExpect(jsonPath("$.text").value("Great tool!"))
            .andExpect(jsonPath("$.authorName").value("Alice"));
        verify(itemService).addComment(eq(2L), eq(1L), any(CreateCommentDto.class));
    }
}
