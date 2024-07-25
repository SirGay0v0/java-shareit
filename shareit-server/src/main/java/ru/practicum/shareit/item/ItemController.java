package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.item.comments.dto.CreateCommentDto;
import ru.practicum.shareit.item.comments.dto.RequestCommentDto;
import ru.practicum.shareit.item.dto.ItemForOwnerDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService service;

    @PostMapping
    public Item add(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                    @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return service.addNewItem(ownerId, itemRequestDto);

    }

    @PatchMapping("/{itemId}")
    public Item update(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                       @PathVariable Long itemId,
                       @RequestBody ItemRequestDto itemRequestDto) throws AccessDeniedException {
        return service.updateItem(ownerId, itemId, itemRequestDto);
    }

    @GetMapping("/{itemId}")
    public ItemForOwnerDto getById(@PathVariable long itemId,
                                   @RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.getItemById(itemId, userId);
    }


    @GetMapping
    public List<ItemForOwnerDto> getAllById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @RequestParam(required = false, defaultValue = "0") int from,
                                            @RequestParam(required = false, defaultValue = "10") int size) {
        return service.getAllById(userId, from, size);
    }

    @GetMapping("/search")
    public List<Item> searchItems(@RequestParam String text,
                                  @RequestParam(required = false, defaultValue = "0") int from,
                                  @RequestParam(required = false, defaultValue = "10") int size) {
        return service.searchItems(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public RequestCommentDto addComment(@RequestBody CreateCommentDto comment,
                                        @RequestHeader("X-Sharer-User-Id") Long userId,
                                        @PathVariable Long itemId) {
        return service.addComment(comment, userId, itemId);
    }
}
