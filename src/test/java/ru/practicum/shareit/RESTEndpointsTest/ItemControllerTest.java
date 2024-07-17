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
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.comments.dto.CreateCommentDto;
import ru.practicum.shareit.item.comments.dto.RequestCommentDto;
import ru.practicum.shareit.item.dto.ItemForOwnerDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ItemController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemControllerTest {

    private final ObjectMapper objectMapper;
    private final MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    private Item item;
    private ItemRequestDto itemRequestDto;
    private ItemForOwnerDto itemForOwnerDto;
    private CreateCommentDto createCommentDto;
    private RequestCommentDto requestCommentDto;

    @BeforeEach
    public void setup() {
        item = new Item()
                .setId(1L)
                .setName("Item name")
                .setDescription("Item description");

        itemRequestDto = new ItemRequestDto()
                .setName("Item name")
                .setDescription("Item description")
                .setAvailable(true);

        itemForOwnerDto = new ItemForOwnerDto()
                .setId(1L)
                .setName("Item name")
                .setDescription("Item description");

        createCommentDto = new CreateCommentDto()
                .setText("Great item!");

        requestCommentDto = new RequestCommentDto()
                .setId(1L)
                .setText("Great item!");
    }

    @Test
    public void testAddItem() throws Exception {
        Mockito.when(itemService.addNewItem(anyLong(),
                any(ItemRequestDto.class))).thenReturn(item);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(item.getId()))
                .andExpect(jsonPath("$.name").value(item.getName()))
                .andExpect(jsonPath("$.description").value(item.getDescription()));
    }

    @Test
    public void testUpdateItem() throws Exception {
        Mockito.when(itemService.updateItem(anyLong(), anyLong(),
                any(ItemRequestDto.class))).thenReturn(item);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(item.getId()))
                .andExpect(jsonPath("$.name").value(item.getName()))
                .andExpect(jsonPath("$.description").value(item.getDescription()));
    }

    @Test
    public void testGetItemById() throws Exception {
        Mockito.when(itemService.getItemById(anyLong(), anyLong())).thenReturn(itemForOwnerDto);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemForOwnerDto.getId()))
                .andExpect(jsonPath("$.name").value(itemForOwnerDto.getName()))
                .andExpect(jsonPath("$.description").value(itemForOwnerDto.getDescription()));
    }

    @Test
    public void testGetAllItems() throws Exception {
        Mockito.when(itemService.getAllById(anyLong(), anyInt(), anyInt()
        )).thenReturn(List.of(itemForOwnerDto));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemForOwnerDto.getId()))
                .andExpect(jsonPath("$[0].name").value(itemForOwnerDto.getName()))
                .andExpect(jsonPath("$[0].description").value(itemForOwnerDto.getDescription()));
    }

    @Test
    public void testSearchItems() throws Exception {
        Mockito.when(itemService.searchItems(anyString(), anyInt(), anyInt()
        )).thenReturn(List.of(item));

        mockMvc.perform(get("/items/search")
                        .param("text", "Item")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(item.getId()))
                .andExpect(jsonPath("$[0].name").value(item.getName()))
                .andExpect(jsonPath("$[0].description").value(item.getDescription()));
    }

    @Test
    public void testAddComment() throws Exception {
        Mockito.when(itemService.addComment(any(CreateCommentDto.class),
                anyLong(), anyLong())).thenReturn(requestCommentDto);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCommentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestCommentDto.getId()))
                .andExpect(jsonPath("$.text").value(requestCommentDto.getText()));
    }
}
