package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemForOwnerDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

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
    public Item add(@RequestHeader("X-Sharer-User-Id") long ownerId,
                    @RequestBody ItemRequestDto itemRequestDto) {
        return service.addNewItem(ownerId, itemRequestDto);

    }

    @PatchMapping("/{itemId}")
    public Item update(@RequestHeader("X-Sharer-User-Id") long ownerId,
                       @PathVariable long itemId,
                       @RequestBody ItemRequestDto itemRequestDto) {
        return service.updateItem(ownerId, itemId, itemRequestDto);
    }

    @GetMapping("/{itemId}")
    public Item getById(@PathVariable long itemId) {
        return service.getItemById(itemId);
    }


    @GetMapping
    public List<ItemForOwnerDto> getAllById(@RequestHeader("X-Sharer-User-Id") long ownerId) {
        return service.getAllById(ownerId);
    }

    @GetMapping("/search")
    public List<Item> searchItems(@RequestParam String text) {
        return service.searchItems(text);
    }
}
