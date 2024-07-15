package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    @PostMapping
    public User add(@Valid @RequestBody User user) {
        return service.create(user);
    }

    @PatchMapping("/{userId}")
    public User update(@PathVariable long userId,
                       @RequestBody User user) {
        return service.update(userId, user);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        service.delete(userId);
    }

    @GetMapping("/{userId}")
    public User getById(@PathVariable Long userId) {
        return service.getById(userId);
    }

    @GetMapping
    public List<User> getAll() {
        return service.getAll();
    }
}
