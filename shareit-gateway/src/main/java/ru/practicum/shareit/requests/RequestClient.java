package ru.practicum.shareit.requests;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.requests.dto.CreateRequestDto;
import ru.practicum.shareit.requests.validation.RequestValidator;

import java.util.Map;

public class RequestClient extends BaseClient {

    private final RequestValidator validator;

    public RequestClient(RestTemplate rest, RequestValidator validator) {
        super(rest);
        this.validator = validator;
    }


    public ResponseEntity<Object> createRequest(long authorId, CreateRequestDto createDto) {
        validator.createValidate(createDto);
        return post("", authorId, createDto);
    }

    public ResponseEntity<Object> getRequestsByAuthorId(long authorId) {
        return get("", authorId);
    }

    public ResponseEntity<Object> getRequestsOtherUsers(long userId, int from, int size) {
        validator.pageRequestValidate(from, size);
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("/all", userId, parameters);
    }

    public ResponseEntity<Object> getRequestById(long requestId, long userId) {
        return get("/" + requestId, userId);
    }

}
