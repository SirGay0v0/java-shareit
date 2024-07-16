package ru.practicum.shareit.ServiceTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.requests.dto.CreateRequestDto;
import ru.practicum.shareit.requests.dto.RequestForUserDto;
import ru.practicum.shareit.requests.dto.ResponseDto;
import ru.practicum.shareit.requests.model.Request;
import ru.practicum.shareit.requests.service.RequestServiceImpl;
import ru.practicum.shareit.requests.storage.RequestStorage;
import ru.practicum.shareit.requests.validation.RequestValidator;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class RequestServiceImplTest {

    @Mock
    private RequestStorage storage;

    @Mock
    private ItemStorage itemStorage;

    @Mock
    private RequestValidator validator;

    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private RequestServiceImpl service;

    private CreateRequestDto createRequestDto;
    private RequestForUserDto requestForUserDto;
    private ResponseDto responseDto;
    private Request request;
    private Long authorId;
    private Item item;
    private PageRequest pageRequest;
    private Page<Request> requestPage;
    private Long requestId;


    @BeforeEach
    public void setup() {
        authorId = 1L;

        createRequestDto = new CreateRequestDto()
                .setDescription("create description");

        request = new Request()
                .setId(1L)
                .setDescription("create description")
                .setAuthor(1L)
                .setCreated(LocalDateTime.now());

        requestForUserDto = new RequestForUserDto()
                .setId(requestId)
                .setDescription("Test Description")
                .setCreated(LocalDateTime.now())
                .setItems(Collections.emptyList());

        responseDto = new ResponseDto()
                .setId(1L)
                .setName("Item Name")
                .setDescription("Item Description");

        item = new Item()
                .setId(1L)
                .setDescription("Description")
                .setOwnerId(1L)
                .setName("Item name")
                .setAvailable(true)
                .setRequestId(null);

        requestPage = new PageImpl<>(List.of(request));

        requestId = 1L;
        pageRequest = PageRequest.of(0, 10);
    }
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void createRequest_shouldReturnRequest_whenValidInput() {
        doNothing().when(validator).createValidate(anyLong(), any(CreateRequestDto.class));
        when(storage.save(any(Request.class))).thenReturn(request);

        Request result = service.create(createRequestDto, 1L);

        assertEquals(result.getDescription(), createRequestDto.getDescription());
        assertEquals(result.getAuthor(), authorId);

        verify(storage, times(1)).save(any(Request.class));
    }
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void createRequest_shouldThrowNotFoundException_whenUserNotExist() {
        doThrow(new NotFoundException("No such user with ID: " + authorId))
                .when(validator).createValidate(anyLong(), any(CreateRequestDto.class));

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.create(createRequestDto, authorId));

        assertEquals("No such user with ID: " + authorId, exception.getMessage());

        verify(validator, times(1)).createValidate(anyLong(),
                any(CreateRequestDto.class));
        verify(storage, never()).save(any(Request.class));
    }
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    public void create_shouldThrowValidationException_whenDescriptionIsNull() {
        createRequestDto.setDescription(null);

        doThrow(new ValidationException("Incorrect field description"))
                .when(validator).createValidate(anyLong(), any(CreateRequestDto.class));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> service.create(createRequestDto, authorId));

        assertEquals("Incorrect field description", exception.getMessage());

        verify(validator, times(1)).createValidate(anyLong(),
                any(CreateRequestDto.class));
        verify(storage, never()).save(any(Request.class));
    }
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void createRequest_shouldThrowValidationException_whenDescriptionIsBlank() {
        createRequestDto.setDescription("");

        doThrow(new ValidationException("Incorrect field description"))
                .when(validator).createValidate(anyLong(), any(CreateRequestDto.class));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> validator.createValidate(authorId, createRequestDto));

        assertEquals("Incorrect field description", exception.getMessage());

        verify(validator, times(1)).createValidate(anyLong(),
                any(CreateRequestDto.class));
        verify(storage, never()).save(any(Request.class));
    }
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    public void getAllById_shouldReturnRequests_whenUserExists() {
        doNothing().when(validator).validateById(anyLong());
        when(storage.findAllByAuthorIs(anyLong())).thenReturn(List.of(request));
        when(itemStorage.findAllByRequestId(anyLong())).thenReturn(List.of(item));
        when(mapper.map(any(Request.class), eq(RequestForUserDto.class)))
                .thenReturn(requestForUserDto);
        when(mapper.map(any(), eq(ResponseDto.class))).thenReturn(responseDto);

        List<RequestForUserDto> result = service.getAllById(authorId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(requestForUserDto.getId(), result.get(0).getId());
        assertEquals(requestForUserDto.getDescription(), result.get(0).getDescription());
        assertEquals(requestForUserDto.getCreated(), result.get(0).getCreated());
        assertEquals(1, result.get(0).getItems().size());
        assertEquals(responseDto.getId(), result.get(0).getItems().get(0).getId());
        assertEquals(responseDto.getName(), result.get(0).getItems().get(0).getName());

        verify(validator, times(1)).validateById(anyLong());
        verify(storage, times(1)).findAllByAuthorIs(anyLong());
        verify(itemStorage, times(1)).findAllByRequestId(anyLong());
        verify(mapper, times(1)).map(any(Request.class),
                eq(RequestForUserDto.class));
        verify(mapper, times(1)).map(any(),
                eq(ResponseDto.class));
    }
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    public void getAllById_shouldThrowNotFoundException_whenUserDoesNotExist() {
        doThrow(new NotFoundException("No such user with ID: " + authorId))
                .when(validator).validateById(anyLong());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.getAllById(authorId));
        assertEquals("No such user with ID: " + authorId, exception.getMessage());

        verify(validator, times(1)).validateById(anyLong());
        verify(storage, never()).findAllByAuthorIs(anyLong());
        verify(itemStorage, never()).findAllByRequestId(anyLong());
        verify(mapper, never()).map(any(Request.class), eq(RequestForUserDto.class));
        verify(mapper, never()).map(any(), eq(ResponseDto.class));
    }
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    public void getAllFromOtherUsers_shouldThrowValidationException_whenInvalidPageRequest() {
        doThrow(new ValidationException("Params from or size has wrong value!"))
                .when(validator).validatePage(anyLong(), any(PageRequest.class));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> service.getAllFromOtherUsers(pageRequest, authorId));
        assertEquals("Params from or size has wrong value!", exception.getMessage());

        verify(validator, times(1)).validatePage(anyLong(),
                any(PageRequest.class));
        verify(storage, never()).findAllByAuthorNotInOrderByCreatedDesc(anyList(),
                any(PageRequest.class));
        verify(itemStorage, never()).findAllByRequestId(anyLong());
        verify(mapper, never()).map(any(Request.class),
                eq(RequestForUserDto.class));
        verify(mapper, never()).map(any(), eq(ResponseDto.class));
    }
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    public void getRequestById_shouldReturnRequest_whenValidInput() {
        doNothing().when(validator).validateRequestAndId(anyLong(), anyLong());
        when(storage.findAllById(anyLong())).thenReturn(request);
        when(mapper.map(any(Request.class),
                eq(RequestForUserDto.class))).thenReturn(requestForUserDto);
        when(itemStorage.findAllByRequestId(anyLong())).thenReturn(List.of(item));
        when(mapper.map(any(), eq(ResponseDto.class))).thenReturn(responseDto);

        RequestForUserDto result = service.getRequestById(requestId, authorId);

        assertNotNull(result);
        assertEquals(requestForUserDto.getId(), result.getId());
        assertEquals(requestForUserDto.getDescription(), result.getDescription());
        assertEquals(requestForUserDto.getCreated(), result.getCreated());
        assertEquals(1, result.getItems().size());
        assertEquals(responseDto.getId(), result.getItems().get(0).getId());
        assertEquals(responseDto.getName(), result.getItems().get(0).getName());

        verify(validator, times(1)).validateRequestAndId(anyLong(),
                anyLong());
        verify(storage, times(1)).findAllById(anyLong());
        verify(itemStorage, times(1)).findAllByRequestId(anyLong());
        verify(mapper, times(1)).map(any(Request.class),
                eq(RequestForUserDto.class));
        verify(mapper, times(1)).map(any(), eq(ResponseDto.class));
    }
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    public void getRequestById_shouldThrowNotFoundException_whenInvalidRequestId() {
        doThrow(new NotFoundException("No such request with ID: " + requestId))
                .when(validator).validateRequestAndId(anyLong(), anyLong());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.getRequestById(requestId, authorId));
        assertEquals("No such request with ID: " + requestId, exception.getMessage());

        verify(validator, times(1))
                .validateRequestAndId(anyLong(), anyLong());
        verify(storage, never()).findAllById(anyLong());
        verify(itemStorage, never()).findAllByRequestId(anyLong());
        verify(mapper, never()).map(any(Request.class), eq(RequestForUserDto.class));
        verify(mapper, never()).map(any(), eq(ResponseDto.class));
    }
}
