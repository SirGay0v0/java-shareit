package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    @PostMapping
    public User add(@RequestBody User user) {
        return service.create(user);
    }

    @PatchMapping("/{userId}")
    public User update(@PathVariable long userId,
                       @RequestBody User user) {
        return service.update(userId, user);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable long userId) {
        service.delete(userId);
    }

    @GetMapping("/{userId}")
    public User getById(@PathVariable long userId) {
        return service.getById(userId);
    }

    @GetMapping
    public List<User> getAll() {
        return service.getAll();
    }
}
