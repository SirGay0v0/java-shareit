package ru.practicum.shareit.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.validation.BookingValidator;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.requests.RequestClient;
import ru.practicum.shareit.requests.validation.RequestValidator;
import ru.practicum.shareit.user.UserClient;


@Configuration
public class WebClientConfig {

    @Value("${shareit-server.url}")
    private String serverUrl;

    private String prefix;

    @Bean
    public BookingClient bookingClient(RestTemplateBuilder builder, BookingValidator validator) {
        prefix = "/bookings";
        var restTemplate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + prefix))
                .build();
        return new BookingClient(restTemplate, validator);
    }

    @Bean
    public ItemClient itemClient(RestTemplateBuilder builder) {
        prefix = "/items";
        var restTemplate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + prefix))
                .build();
        return new ItemClient(restTemplate);
    }

    @Bean
    public RequestClient requestClient(RestTemplateBuilder builder, RequestValidator validator) {
        prefix = "/requests";
        var restTemplate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + prefix))
                .build();
        return new RequestClient(restTemplate, validator);
    }

    @Bean
    public UserClient userClient(RestTemplateBuilder builder) {
        prefix = "/users";
        var restTemplate = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + prefix))
                .build();
        return new UserClient(restTemplate);
    }
}

