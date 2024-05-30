package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemForOwner;
import ru.practicum.shareit.item.dto.ItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService service;

    @PostMapping
    public Item add(@RequestHeader("X-Sharer-User-Id") long ownerId,
                    @RequestBody ItemRequest itemRequest) {
        return service.addNewItem(ownerId, itemRequest);

    }

    @PatchMapping("/{itemId}")
    public Item update(@RequestHeader("X-Sharer-User-Id") long ownerId,
                       @PathVariable long itemId,
                       @RequestBody ItemRequest itemRequest) {
        return service.updateItem(ownerId, itemId, itemRequest);
    }

    @GetMapping("/{itemId}")
    public Item getById(@PathVariable long itemId) {
        return service.getItemById(itemId);
    }


    @GetMapping
    public List<ItemForOwner> getAllById(@RequestHeader("X-Sharer-User-Id") long ownerId) {
        return service.getAllById(ownerId);
    }

    @GetMapping("/search")
    public List<Item> searchItems(@RequestParam String text) {
        return service.searchItems(text);
    }
}
