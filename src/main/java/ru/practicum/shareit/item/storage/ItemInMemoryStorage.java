package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ItemInMemoryStorage implements ItemStorage {
    private final Map<Long, Item> itemMap = new HashMap<>();
    private long generatedId = 1;

    @Override
    public Item create(Item item) {
        item.setId(generatedId);
        itemMap.put(generatedId, item);
        generatedId++;
        return item;
    }

    @Override
    public Item update(Item item) {
        itemMap.put(item.getId(), item);
        return itemMap.get(item.getId());
    }

    @Override
    public Item getById(long itemId) {
        return itemMap.get(itemId);
    }

    @Override
    public List<Item> getAllById(long ownerId) {
        return itemMap.values().stream()
                .filter(item -> item.getOwnerId() == ownerId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> searchItems(String request) {
        return itemMap.values().stream()
                .filter(item -> item.getName().toLowerCase().contains(request.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(request.toLowerCase()))
                .collect(Collectors.toList());
    }
}
