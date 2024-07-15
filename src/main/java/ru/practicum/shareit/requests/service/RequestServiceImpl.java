package ru.practicum.shareit.requests.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.requests.dto.CreateRequestDto;
import ru.practicum.shareit.requests.dto.RequestForUserDto;
import ru.practicum.shareit.requests.dto.ResponseDto;
import ru.practicum.shareit.requests.model.Request;
import ru.practicum.shareit.requests.storage.RequestStorage;
import ru.practicum.shareit.requests.validation.RequestValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final ModelMapper mapper;
    private final RequestStorage storage;
    private final ItemStorage itemStorage;
    private final RequestValidator validator;

    @Override
    public Request create(CreateRequestDto createDto, Long authorId) {
        validator.createValidate(authorId, createDto);
        Request request = new Request()
                .setAuthor(authorId)
                .setDescription(createDto.getDescription())
                .setCreated(LocalDateTime.now());
        return storage.save(request);
    }

    @Override
    public List<RequestForUserDto> getAllById(Long authorId) {
        validator.validateById(authorId);
        List<Request> requestlist = storage.findAllByAuthorIs(authorId);
        List<RequestForUserDto> resultList = new ArrayList<>();
        for (Request request : requestlist) {
            List<ResponseDto> responsList = itemStorage.findAllByRequestId(request.getId()).stream()
                    .map(response -> mapper.map(response, ResponseDto.class))
                    .collect(Collectors.toList());

            RequestForUserDto requestForUserDto = mapper.map(request, RequestForUserDto.class);
            requestForUserDto.setItems(responsList);
            resultList.add(requestForUserDto);
        }
        return resultList;
    }

    @Override
    public List<RequestForUserDto> getAllFromOtherUsers(PageRequest pageRequest, Long userId) {
        validator.validatePage(userId, pageRequest);
        Page<Request> requestPage = storage.findAllByAuthorNotInOrderByCreatedDesc(List.of(userId), pageRequest);

        List<RequestForUserDto> requestDto = requestPage.getContent().stream()
                .map(request -> mapper.map(request, RequestForUserDto.class))
                .collect(Collectors.toList());

        for (RequestForUserDto request : requestDto) {
            List<ResponseDto> responsList = itemStorage.findAllByRequestId(request.getId()).stream()
                    .map(response -> mapper.map(response, ResponseDto.class))
                    .collect(Collectors.toList());

            RequestForUserDto requestForUserDto = mapper.map(request, RequestForUserDto.class);
            requestForUserDto.setItems(responsList);
        }
        return requestDto;
    }

    @Override
    public RequestForUserDto getRequestById(Long requestId, Long userId) {
        validator.validateRequestAndId(requestId, userId);
        Request request = storage.findAllById(requestId);

        RequestForUserDto requestDto = mapper.map(request, RequestForUserDto.class);
        List<ResponseDto> responseDtoList = itemStorage.findAllByRequestId(requestId).stream()
                .map(response -> mapper.map(response, ResponseDto.class))
                .collect(Collectors.toList());
        requestDto.setItems(responseDtoList);
        return requestDto;
    }
}
