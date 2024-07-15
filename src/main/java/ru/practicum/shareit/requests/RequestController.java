package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
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
        return service.getAllFromOtherUsers(PageRequest.of(from, size), authorId);
    }

    @GetMapping("/{requestId}")
    public RequestForUserDto getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @PathVariable Long requestId) {
        return service.getRequestById(requestId, userId);
    }
}
