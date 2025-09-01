package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserService userService;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(1L, "Name", "email@ya.ru");
    }

    @Test
    void createUserTest() throws Exception {
        UserDto userCreateDto = new UserDto(null, "Name", "test@ya.ru");

        when(userService.createUser(any(UserDto.class))).thenReturn(userDto);

        mvc.perform(post("/users")
                .content(mapper.writeValueAsString(userCreateDto))
                .characterEncoding(java.nio.charset.StandardCharsets.UTF_8)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .accept(org.springframework.http.MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Name"))
            .andExpect(jsonPath("$.email").value("email@ya.ru"));

        verify(userService).createUser(any(UserDto.class));
    }

    @Test
    void getUserByIdTest() throws Exception {
        when(userService.getUserById(eq(1L))).thenReturn(userDto);

        mvc.perform(get("/users/{id}", 1L)
                .accept(org.springframework.http.MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Name"))
            .andExpect(jsonPath("$.email").value("email@ya.ru"));

        verify(userService).getUserById(1L);
    }

    @Test
    void getAllUsersTest() throws Exception {
        when(userService.getAllUsers()).thenReturn(java.util.List.of(userDto));

        mvc.perform(get("/users")
                .accept(org.springframework.http.MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(1)))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].email").value("email@ya.ru"));

        verify(userService).getAllUsers();
    }

    @Test
    void updateUserTest() throws Exception {
        UserDto patchDto = new UserDto(null, "NewName", null);
        UserDto updated = new UserDto(1L, "NewName", "email@ya.ru");

        when(userService.updateUser(eq(1L), any(UserDto.class))).thenReturn(updated);

        mvc.perform(patch("/users/{id}", 1L)
                .content(mapper.writeValueAsString(patchDto))
                .characterEncoding(java.nio.charset.StandardCharsets.UTF_8)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .accept(org.springframework.http.MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("NewName"))
            .andExpect(jsonPath("$.email").value("email@ya.ru"));

        verify(userService).updateUser(eq(1L), any(UserDto.class));
    }

    @Test
    void deleteUserTest() throws Exception {
        mvc.perform(delete("/users/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(1L);
    }
}