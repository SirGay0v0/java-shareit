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
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.requests.RequestController;
import ru.practicum.shareit.requests.dto.CreateRequestDto;
import ru.practicum.shareit.requests.dto.RequestForUserDto;
import ru.practicum.shareit.requests.model.Request;
import ru.practicum.shareit.requests.service.RequestService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(RequestController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RequestControllerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @MockBean
    private RequestService requestService;

    private Request request;
    private CreateRequestDto createRequestDto;
    private RequestForUserDto requestForUserDto;

    @BeforeEach
    public void setup() {
        request = new Request()
                .setId(1L)
                .setAuthor(1L)
                .setDescription("Test description")
                .setCreated(LocalDateTime.now());

        createRequestDto = new CreateRequestDto()
                .setDescription("Test description");

        requestForUserDto = new RequestForUserDto()
                .setId(1L)
                .setDescription("Test description")
                .setCreated(LocalDateTime.now())
                .setItems(Collections.emptyList());
    }

    @Test
    public void testCreateRequest() throws Exception {
        Mockito.when(requestService.create(any(CreateRequestDto.class),
                anyLong())).thenReturn(request);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(request.getId()))
                .andExpect(jsonPath("$.description").value(request.getDescription()));
    }

    @Test
    public void testGetRequests() throws Exception {
        Mockito.when(requestService.getAllById(anyLong())).thenReturn(List.of(requestForUserDto));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(requestForUserDto.getId()))
                .andExpect(jsonPath("$[0].description").value(requestForUserDto.getDescription()));
    }

    @Test
    public void testGetRequestsAnotherUsers() throws Exception {
        Mockito.when(requestService.getAllFromOtherUsers(anyLong(), anyInt(), anyInt()
        )).thenReturn(List.of(requestForUserDto));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(requestForUserDto.getId()))
                .andExpect(jsonPath("$[0].description").value(requestForUserDto.getDescription()));
    }

    @Test
    public void testGetRequestById() throws Exception {
        Mockito.when(requestService.getRequestById(anyLong(),
                anyLong())).thenReturn(requestForUserDto);

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestForUserDto.getId()))
                .andExpect(jsonPath("$.description").value(requestForUserDto.getDescription()));
    }
}
