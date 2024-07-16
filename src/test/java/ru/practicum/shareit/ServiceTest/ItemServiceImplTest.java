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
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.item.comments.dto.CreateCommentDto;
import ru.practicum.shareit.item.comments.dto.RequestCommentDto;
import ru.practicum.shareit.item.comments.model.Comment;
import ru.practicum.shareit.item.comments.storage.CommentsStorage;
import ru.practicum.shareit.item.dto.ItemForOwnerDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.LastBookingDto;
import ru.practicum.shareit.item.dto.NextBookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.item.validation.ItemValidator;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {

    @Mock
    private ItemStorage itemStorage;

    @Mock
    private ModelMapper mapper;

    @Mock
    private ItemValidator validator;

    @Mock
    private BookingStorage bookingStorage;

    @Mock
    private CommentsStorage commentsStorage;

    @Mock
    private UserStorage userStorage;

    @InjectMocks
    private ItemServiceImpl service;

    private Long ownerId;
    private Long itemId;
    private Long userId;
    private Item item;
    private ItemRequestDto itemRequestDto;
    private ItemForOwnerDto itemForOwnerDto;
    private CreateCommentDto createCommentDto;
    private Comment comment;
    private RequestCommentDto requestCommentDto;
    private Booking booking;
    private NextBookingDto nextBookingDto;
    private LastBookingDto lastBookingDto;
    private PageRequest pageRequest;

    @BeforeEach
    void setUp() {
        ownerId = 1L;
        itemId = 1L;
        userId = 2L;

        item = new Item();
        item.setId(itemId);
        item.setOwnerId(ownerId);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);

        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setName("Updated Item");
        itemRequestDto.setDescription("Updated Description");
        itemRequestDto.setAvailable(true);

        itemForOwnerDto = new ItemForOwnerDto();
        itemForOwnerDto.setId(itemId);
        itemForOwnerDto.setName("Test Item");
        itemForOwnerDto.setDescription("Test Description");

        createCommentDto = new CreateCommentDto();
        createCommentDto.setText("Test Comment");

        comment = new Comment();
        comment.setId(1L);
        comment.setText("Test Comment");
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());

        requestCommentDto = new RequestCommentDto();
        requestCommentDto.setId(1L);
        requestCommentDto.setText("Test Comment");

        booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStatus(Status.APPROVED);

        nextBookingDto = new NextBookingDto();
        nextBookingDto.setId(1L);
        nextBookingDto.setBookerId(1L);

        lastBookingDto = new LastBookingDto();
        lastBookingDto.setId(2L);
        lastBookingDto.setBookerId(1L);

        pageRequest = PageRequest.of(0, 10);
    }
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void addNewItem_shouldAddNewItem_whenValidInput() {
        when(mapper.map(any(ItemRequestDto.class),
                eq(Item.class))).thenReturn(item);
        when(itemStorage.save(any(Item.class))).thenReturn(item);

        Item result = service.addNewItem(ownerId, itemRequestDto);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        verify(validator, times(1)).validateByExistingUser(ownerId);
        verify(mapper, times(1)).map(any(ItemRequestDto.class),
                eq(Item.class));
        verify(itemStorage, times(1)).save(any(Item.class));
    }
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void updateItem_shouldUpdateItem_whenValidInput() throws AccessDeniedException {
        when(validator.validateItem(anyLong())).thenReturn(item);
        when(itemStorage.save(any(Item.class))).thenReturn(item);

        Item result = service.updateItem(ownerId, itemId, itemRequestDto);

        assertNotNull(result);
        assertEquals(itemRequestDto.getName(), result.getName());
        verify(validator, times(1)).validateItem(anyLong());
        verify(itemStorage, times(1)).save(any(Item.class));
    }
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void updateItem_shouldThrowAccessDeniedException_whenUserIsNotOwner() {
        when(validator.validateItem(anyLong())).thenReturn(item);

        assertThrows(AccessDeniedException.class,
                () -> service.updateItem(userId, itemId, itemRequestDto));
        verify(validator, times(1)).validateItem(anyLong());
        verify(itemStorage, never()).save(any(Item.class));
    }
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void getItemById_shouldReturnItemForOwnerDto_whenValidInput() {
        when(validator.validateItem(anyLong())).thenReturn(item);
        when(mapper.map(any(Item.class),
                eq(ItemForOwnerDto.class))).thenReturn(itemForOwnerDto);
        when(bookingStorage.findByItemIdAndStatus(anyLong(),
                eq(Status.APPROVED))).thenReturn(List.of(booking));
        when(commentsStorage.findByItemId(anyLong())).thenReturn(List.of(comment));
        when(mapper.map(any(Comment.class),
                eq(RequestCommentDto.class))).thenReturn(requestCommentDto);

        // Mock the getNext and getLast methods to return expected NextBookingDto and LastBookingDto
        when(mapper.map(any(), eq(NextBookingDto.class))).thenReturn(nextBookingDto);
        when(mapper.map(any(), eq(LastBookingDto.class))).thenReturn(lastBookingDto);

        ItemForOwnerDto result = service.getItemById(itemId, ownerId);

        assertNotNull(result);
        assertEquals(itemForOwnerDto.getId(), result.getId());
        verify(validator, times(1)).validateItem(anyLong());
        verify(mapper, times(1)).map(any(Item.class),
                eq(ItemForOwnerDto.class));
        verify(bookingStorage, times(1))
                .findByItemIdAndStatus(anyLong(), eq(Status.APPROVED));
        verify(commentsStorage, times(1)).findByItemId(anyLong());
        verify(mapper, times(1)).map(any(Comment.class),
                eq(RequestCommentDto.class));
    }
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void getAllById_shouldReturnItemsForOwner_whenValidInput() {
        Page<Item> itemsPage = new PageImpl<>(List.of(item));
        when(itemStorage.findByOwnerId(any(PageRequest.class),
                anyLong())).thenReturn(itemsPage);
        when(mapper.map(any(Item.class),
                eq(ItemForOwnerDto.class))).thenReturn(itemForOwnerDto);
        when(bookingStorage.findByItemId(anyLong())).thenReturn(List.of(booking));
        when(commentsStorage.findByItemId(anyLong())).thenReturn(List.of(comment));
        when(mapper.map(any(Comment.class),
                eq(RequestCommentDto.class))).thenReturn(requestCommentDto);

        when(mapper.map(any(),
                eq(NextBookingDto.class))).thenReturn(nextBookingDto);
        when(mapper.map(any(),
                eq(LastBookingDto.class))).thenReturn(lastBookingDto);

        List<ItemForOwnerDto> result = service.getAllById(pageRequest, ownerId);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(itemStorage, times(1))
                .findByOwnerId(any(PageRequest.class), anyLong());
        verify(mapper, times(1)).map(any(Item.class),
                eq(ItemForOwnerDto.class));
        verify(bookingStorage, times(1)).findByItemId(anyLong());
        verify(commentsStorage, times(1)).findByItemId(anyLong());
        verify(mapper, times(1)).map(any(Comment.class),
                eq(RequestCommentDto.class));
    }
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void searchItems_shouldReturnItems_whenRequestIsNotBlank() {
        Page<Item> itemsPage = new PageImpl<>(List.of(item));
        when(itemStorage.findByNameContainsIgnoringCaseOrDescriptionContainsIgnoringCaseAndAvailableIsTrue(
                anyString(), anyString(), any(PageRequest.class)))
                .thenReturn(itemsPage);

        List<Item> result = service.searchItems(pageRequest, "Test");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(itemStorage, times(1))
                .findByNameContainsIgnoringCaseOrDescriptionContainsIgnoringCaseAndAvailableIsTrue(anyString(),
                        anyString(), any(PageRequest.class));
    }
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @Test
    void addComment_shouldAddComment_whenValidInput() {
        when(mapper.map(any(CreateCommentDto.class),
                eq(Comment.class))).thenReturn(comment);
        when(commentsStorage.save(any(Comment.class))).thenReturn(comment);
        when(mapper.map(any(Comment.class),
                eq(RequestCommentDto.class))).thenReturn(requestCommentDto);
        when(userStorage.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(itemStorage.findById(anyLong())).thenReturn(Optional.of(item));

        RequestCommentDto result = service.addComment(createCommentDto, userId, itemId);

        assertNotNull(result);
        assertEquals(requestCommentDto.getId(), result.getId());
        verify(validator, times(1))
                .validateComment(any(CreateCommentDto.class), anyLong(), anyLong());
        verify(mapper, times(1)).map(any(CreateCommentDto.class),
                eq(Comment.class));
        verify(commentsStorage, times(1)).save(any(Comment.class));
        verify(mapper, times(1)).map(any(Comment.class),
                eq(RequestCommentDto.class));
        verify(userStorage, times(1)).findById(anyLong());
        verify(itemStorage, times(1)).findById(anyLong());
    }
}
