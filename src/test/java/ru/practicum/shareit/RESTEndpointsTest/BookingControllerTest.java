package ru.practicum.shareit.RESTEndpointsTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(BookingController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingControllerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    private CreateBookingDto createBookingDto;
    private RequestBookingDto requestBookingDto;

    @BeforeEach
    public void setup() {
        createBookingDto = new CreateBookingDto()
                .setItemId(1L)
                .setStart(LocalDateTime.parse("2024-01-01T10:00:00"))
                .setEnd(LocalDateTime.parse("2024-01-02T10:00:00"));

        requestBookingDto = new RequestBookingDto()
                .setId(1L)
                .setStart(LocalDateTime.parse("2024-01-01T10:00:00"))
                .setEnd(LocalDateTime.parse("2024-01-02T10:00:00"));
    }

    @Test
    public void testAddBooking() throws Exception {
        Mockito.when(bookingService.createBooking(any(CreateBookingDto.class),
                anyLong())).thenReturn(requestBookingDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createBookingDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestBookingDto.getId()))
                .andExpect(jsonPath("$.start").value("2024-01-01T10:00:00"))
                .andExpect(jsonPath("$.end").value("2024-01-02T10:00:00"));
    }

    @Test
    public void testConfirmBooking() throws Exception {
        Mockito.when(bookingService.confirm(anyLong(), anyBoolean(),
                anyLong())).thenReturn(requestBookingDto);

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestBookingDto.getId()))
                .andExpect(jsonPath("$.start").value("2024-01-01T10:00:00"))
                .andExpect(jsonPath("$.end").value("2024-01-02T10:00:00"));
    }

    @Test
    public void testGetBookingById() throws Exception {
        Mockito.when(bookingService.getBookingById(anyLong(),
                anyLong())).thenReturn(requestBookingDto);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestBookingDto.getId()))
                .andExpect(jsonPath("$.start").value("2024-01-01T10:00:00"))
                .andExpect(jsonPath("$.end").value("2024-01-02T10:00:00"));
    }

    @Test
    public void testGetListBookingsByUser() throws Exception {
        Mockito.when(bookingService.getListUserBookingsByStatus(anyString(),
                        anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(requestBookingDto));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(requestBookingDto.getId()))
                .andExpect(jsonPath("$[0].start").value("2024-01-01T10:00:00"))
                .andExpect(jsonPath("$[0].end").value("2024-01-02T10:00:00"));
    }

    @Test
    public void testGetListBookingsByOwner() throws Exception {
        Mockito.when(bookingService.getListOwnerBookingsByStatus(anyString(),
                        anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(requestBookingDto));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(requestBookingDto.getId()))
                .andExpect(jsonPath("$[0].start").value("2024-01-01T10:00:00"))
                .andExpect(jsonPath("$[0].end").value("2024-01-02T10:00:00"));
    }
}
