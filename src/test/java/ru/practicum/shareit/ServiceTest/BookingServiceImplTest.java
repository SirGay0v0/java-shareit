package ru.practicum.shareit.ServiceTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.dto.ItemBookingDto;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.dto.UserBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.booking.validation.BookingValidator;
import ru.practicum.shareit.exception.InternalServerException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {

    @Mock
    private BookingStorage storage;

    @Mock
    private ModelMapper mapper;

    @Mock
    private BookingValidator validator;

    @InjectMocks
    private BookingServiceImpl service;

    private User user;
    private Item item;
    private Booking booking;
    private CreateBookingDto createBookingDto;
    private RequestBookingDto requestBookingDto;

    @BeforeEach
    public void setup() {
        user = new User()
                .setId(1L)
                .setName("User")
                .setEmail("user@example.com");

        item = new Item()
                .setId(1L)
                .setName("Item")
                .setDescription("Description")
                .setAvailable(true)
                .setOwnerId(1L)
                .setRequestId(null);

        booking = new Booking()
                .setId(1L)
                .setStart(LocalDateTime.now().minusDays(1))
                .setEnd(LocalDateTime.now().plusDays(1))
                .setItem(item)
                .setBooker(user)
                .setStatus(Status.WAITING);

        createBookingDto = new CreateBookingDto()
                .setStart(LocalDateTime.now().minusDays(1))
                .setEnd(LocalDateTime.now().plusDays(1))
                .setItemId(1L);

        requestBookingDto = new RequestBookingDto()
                .setId(1L)
                .setStart(LocalDateTime.now().minusDays(1))
                .setEnd(LocalDateTime.now().plusDays(1))
                .setStatus(Status.WAITING)
                .setItem(null)
                .setBooker(null);
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void createBooking_shouldCreateBooking_whenValidInput() {

        User user = new User()
                .setId(1L);

        Item item = new Item()
                .setId(2L)
                .setOwnerId(3L);

        CreateBookingDto createBookingDto = new CreateBookingDto()
                .setItemId(2L)
                .setStart(LocalDateTime.now().plusDays(1))
                .setEnd(LocalDateTime.now().plusDays(2));

        Booking booking = new Booking()
                .setBooker(user)
                .setItem(item)
                .setStatus(Status.WAITING);


        RequestBookingDto requestBookingDto = new RequestBookingDto();

        when(validator.validateBooker(anyLong())).thenReturn(user);
        when(validator.validateItem(any(CreateBookingDto.class))).thenReturn(item);
        when(storage.save(any(Booking.class))).thenReturn(booking);

        doReturn(new UserBookingDto()).when(mapper).map(any(User.class), eq(UserBookingDto.class));
        doReturn(new ItemBookingDto()).when(mapper).map(any(Item.class), eq(ItemBookingDto.class));
        doReturn(booking).when(mapper).map(any(CreateBookingDto.class), eq(Booking.class));
        doReturn(requestBookingDto).when(mapper).map(any(Booking.class), eq(RequestBookingDto.class));

        RequestBookingDto result = service.createBooking(createBookingDto, 1L);

        assertNotNull(result);
        verify(storage, times(1)).save(any(Booking.class));
        verify(mapper, times(1)).map(any(User.class), eq(UserBookingDto.class));
        verify(mapper, times(1)).map(any(Item.class), eq(ItemBookingDto.class));
        verify(mapper, times(1)).map(any(CreateBookingDto.class), eq(Booking.class));
        verify(mapper, times(1)).map(any(Booking.class), eq(RequestBookingDto.class));
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    public void testCreateBookingOwner() {
        when(validator.validateBooker(anyLong())).thenReturn(user);
        item.setOwnerId(user.getId());
        when(validator.validateItem(any(CreateBookingDto.class))).thenReturn(item);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> service.createBooking(createBookingDto, 1L));
        assertEquals("Owner can't book his own items", exception.getMessage());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    public void testConfirmBooking() {
        when(validator.validateBooking(anyLong())).thenReturn(booking);
        when(storage.save(any(Booking.class))).thenReturn(booking);
        when(mapper.map(any(), eq(RequestBookingDto.class))).thenReturn(requestBookingDto);

        RequestBookingDto result = service.confirm(1L, true, user.getId());

        assertNotNull(result);
        assertEquals(Status.APPROVED, booking.getStatus());
        verify(storage, times(1)).save(any(Booking.class));
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    public void testConfirmBookingNotOwner() {
        when(validator.validateBooking(anyLong())).thenReturn(booking);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> service.confirm(1L, true, 2L));
        assertEquals("Only owner of item can change status", exception.getMessage());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    public void testConfirmBookingAlreadyApproved() {
        booking.setStatus(Status.APPROVED);
        when(validator.validateBooking(anyLong())).thenReturn(booking);

        ValidationException exception = assertThrows(ValidationException.class, () -> service.confirm(1L, true, 1L));
        assertEquals("Booking already approved", exception.getMessage());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    public void testGetBookingById() {
        when(validator.validateBooker(anyLong())).thenReturn(user);
        when(validator.validateBooking(anyLong())).thenReturn(booking);
        when(mapper.map(any(), eq(RequestBookingDto.class))).thenReturn(requestBookingDto);

        RequestBookingDto result = service.getBookingById(1L, 1L);

        assertNotNull(result);
        verify(validator, times(1)).validateBooker(anyLong());
        verify(validator, times(1)).validateBooking(anyLong());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    public void testGetBookingByIdNotAccessible() {
        when(validator.validateBooker(anyLong())).thenReturn(user);
        when(validator.validateBooking(anyLong())).thenReturn(booking);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> service.getBookingById(1L, 2L));
        assertEquals("User with id 2haven't access to this booking", exception.getMessage());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    public void testGetListUserBookingsByStatus() {
        when(storage.findByBookerIdOrderByStartDesc(anyLong(), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));
        when(mapper.map(any(), eq(RequestBookingDto.class))).thenReturn(requestBookingDto);

        List<RequestBookingDto> result = service.getListUserBookingsByStatus("ALL", 1L, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(storage, times(1)).findByBookerIdOrderByStartDesc(anyLong(), any(PageRequest.class));
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    public void testGetListOwnerBookingsByStatus() {
        when(storage.findByItemOwnerIdOrderByStartDesc(anyLong(), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(booking)));
        when(mapper.map(any(), eq(RequestBookingDto.class))).thenReturn(requestBookingDto);

        List<RequestBookingDto> result = service.getListOwnerBookingsByStatus("ALL", 1L, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(storage, times(1)).findByItemOwnerIdOrderByStartDesc(anyLong(), any(PageRequest.class));
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    public void testResultListByStateAll() {
        List<Booking> bookings = Collections.singletonList(booking);
        when(mapper.map(any(Booking.class), eq(RequestBookingDto.class))).thenReturn(requestBookingDto);

        List<RequestBookingDto> result = service.resultListByState("ALL", bookings);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    public void testResultListByStateCurrent() {
        booking.setStart(LocalDateTime.now().minusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(1));
        List<Booking> bookings = Collections.singletonList(booking);
        when(mapper.map(any(Booking.class), eq(RequestBookingDto.class))).thenReturn(requestBookingDto);

        List<RequestBookingDto> result = service.resultListByState("CURRENT", bookings);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    public void testResultListByStatePast() {
        booking.setEnd(LocalDateTime.now().minusDays(1));
        List<Booking> bookings = Collections.singletonList(booking);
        when(storage.findAllByEndIsBefore(any(LocalDateTime.class))).thenReturn(bookings);
        when(mapper.map(any(Booking.class), eq(RequestBookingDto.class))).thenReturn(requestBookingDto);

        List<RequestBookingDto> result = service.resultListByState("PAST", bookings);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    public void testResultListByStateFuture() {
        booking.setStart(LocalDateTime.now().plusDays(1));
        List<Booking> bookings = Collections.singletonList(booking);
        when(storage.findAllByStartIsAfter(any(LocalDateTime.class))).thenReturn(bookings);
        when(mapper.map(any(Booking.class), eq(RequestBookingDto.class))).thenReturn(requestBookingDto);

        List<RequestBookingDto> result = service.resultListByState("FUTURE", bookings);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    public void testResultListByStateWaiting() {
        booking.setStatus(Status.WAITING);
        List<Booking> bookings = List.of(booking);
        when(mapper.map(
                any(Booking.class),
                eq(RequestBookingDto.class)))
                .thenReturn(requestBookingDto);

        List<RequestBookingDto> result = service.resultListByState("WAITING", bookings);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    public void testResultListByStateRejected() {
        booking.setStatus(Status.REJECTED);
        List<Booking> bookings = List.of(booking);
        when(mapper.map(
                any(Booking.class),
                eq(RequestBookingDto.class)))
                .thenReturn(requestBookingDto);

        List<RequestBookingDto> result = service.resultListByState("REJECTED", bookings);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    public void testResultListByStateUnknown() {
        List<Booking> bookings = Collections.singletonList(booking);

        InternalServerException exception = assertThrows(
                InternalServerException.class,
                () -> service.resultListByState("UNKNOWN", bookings));
        assertEquals("Unknown state: UNKNOWN", exception.getMessage());
    }
}
