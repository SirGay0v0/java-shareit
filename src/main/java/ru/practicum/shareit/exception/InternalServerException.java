package ru.practicum.shareit.exception;

public class InternalServerException extends InternalError {
    public InternalServerException(String message) {
        super(message);
    }
}
