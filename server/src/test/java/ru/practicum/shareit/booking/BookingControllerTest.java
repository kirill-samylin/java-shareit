package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    private BookingDto bookingDto;
    private CreateBookingDto createBookingDto;

    @BeforeEach
    void setUp() {
        createBookingDto = new CreateBookingDto(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3), null);
        bookingDto = new BookingDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3), null, null, BookingStatus.WAITING);
    }

    @Test
    void createBookingTest() throws Exception {
        when(bookingService.createBooking(eq(2L), any(CreateBookingDto.class)))
                .thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createBookingDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("WAITING"));

        verify(bookingService).createBooking(eq(2L), any(CreateBookingDto.class));
    }

    @Test
    void approveBookingTest() throws Exception {
        // после подтверждения пусть статус станет APPROVED
        BookingDto approved = new BookingDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3), null, null, BookingStatus.APPROVED);

        when(bookingService.approveBooking(eq(1L), eq(2L), eq(true)))
                .thenReturn(approved);

        mvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 2L)
                        .param("approved", "true")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("APPROVED"));

        verify(bookingService).approveBooking(1L, 2L, true);
    }

    @Test
    void getBookingByIdTest() throws Exception {
        when(bookingService.getBookingById(eq(1L), eq(2L)))
                .thenReturn(bookingDto);

        mvc.perform(get("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 2L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("WAITING"));

        verify(bookingService).getBookingById(1L, 2L);
    }

    @Test
    void getBookingsByBookerTest() throws Exception {
        when(bookingService.getBookingsByBooker(eq(2L), eq("ALL")))
                .thenReturn(List.of(bookingDto));
        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 2L)
                        .param("state", "ALL")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].status").value("WAITING"));
        verify(bookingService).getBookingsByBooker(2L, "ALL");
    }

    @Test
    void getBookingsByOwnerTest() throws Exception {
        when(bookingService.getBookingsByOwner(eq(2L), eq("ALL")))
                .thenReturn(List.of(bookingDto));
        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 2L)
                        .param("state", "ALL")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].status").value("WAITING"));
        verify(bookingService).getBookingsByOwner(2L, "ALL");
    }

    @Test
    void deleteBookingTest() throws Exception {
        mvc.perform(delete("/bookings/{bookingId}", 1L))
                .andExpect(status().isNoContent());
        verify(bookingService).deleteBooking(1L);
    }
}