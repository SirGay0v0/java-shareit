package ru.practicum.shareit.requests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.requests.dto.CreateRequestDto;
import ru.practicum.shareit.requests.validation.RequestValidator;

import java.util.Map;

@Service
public class RequestClient extends BaseClient {

    private static final String API_PREFIX = "/requests";
    private final RequestValidator validator;

    @Autowired
    public RequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder, RequestValidator validator) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
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
