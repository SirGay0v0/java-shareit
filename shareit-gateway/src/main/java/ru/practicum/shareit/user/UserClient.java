package ru.practicum.shareit.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.User;


public class UserClient extends BaseClient {
    public UserClient(RestTemplate rest) {
        super(rest);
    }

    public ResponseEntity<Object> addUser(User user) {
        return post("", user);
    }

    public ResponseEntity<Object> updateUser(long userId, User user) {
        return patch("/" + userId, user);
    }

    public void deleteUser(long userId) {
        delete("/" + userId, userId);
    }

    public ResponseEntity<Object> getUserById(long userId) {
        return get("/" + userId, userId);
    }

    public ResponseEntity<Object> getAllUsers() {
        return get("");
    }
}
