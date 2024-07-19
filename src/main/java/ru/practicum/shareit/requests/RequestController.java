package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.requests.dto.CreateRequestDto;
import ru.practicum.shareit.requests.dto.RequestForUserDto;
import ru.practicum.shareit.requests.model.Request;
import ru.practicum.shareit.requests.service.RequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/requests")
public class RequestController {

    private final RequestService service;

    @PostMapping
    public Request create(@RequestHeader("X-Sharer-User-Id") Long authorId,
                          @RequestBody CreateRequestDto createDto) {
        return service.create(createDto, authorId);
    }

    @GetMapping
    public List<RequestForUserDto> getRequests(@RequestHeader("X-Sharer-User-Id") Long authorId) {
        return service.getAllById(authorId);
    }

    @GetMapping("/all")
    public List<RequestForUserDto> getRequestsAnotherUsers(@RequestParam(required = false, defaultValue = "0") int from,
                                                           @RequestParam(required = false, defaultValue = "1") int size,
                                                           @RequestHeader("X-Sharer-User-Id") Long authorId) {
        return service.getAllFromOtherUsers(authorId,from, size);
    }

    @GetMapping("/{requestId}")
    public RequestForUserDto getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @PathVariable Long requestId) {
        return service.getRequestById(requestId, userId);
    }
}
