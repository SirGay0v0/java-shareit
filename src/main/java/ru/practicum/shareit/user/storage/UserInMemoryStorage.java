package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Component
public class UserInMemoryStorage implements UserStorage {
    private final Map<Long, User> userMap = new HashMap<>();
    private long generateId = 1;

    @Override
    public User create(User user) {
        user.setId(generateId);
        userMap.put(generateId, user);
        generateId++;
        return userMap.get(generateId - 1);
    }

    @Override
    public User update(User user) {
        userMap.put(user.getId(), user);
        return userMap.get(user.getId());
    }

    @Override
    public void delete(long userId) {
        userMap.remove(userId);
    }

    @Override
    public User getById(long id) {
        return userMap.get(id);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(userMap.values());
    }

    @Override
    public boolean getByEmail(String request) {
        Optional<User> opt = userMap.values().stream()
                .filter(user -> StringUtils.hasText(user.getEmail()) && user.getEmail().equals(request))
                .findFirst();
        return opt.isPresent();
    }
}
