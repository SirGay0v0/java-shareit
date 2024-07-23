package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@RestControllerAdvice
public class ErrorHandler {
//    //Docker version
//    @ExceptionHandler(HttpClientErrorException.NotFound.class)
//    public ResponseEntity<ErrorResponse> handleHttpClientErrorNotFound(final HttpClientErrorException.NotFound exception) {
//        return new ResponseEntity<>(new ErrorResponse(exception.getResponseBodyAsString()),
//                HttpStatus.NOT_FOUND);
//    }
//
//    //Local version
//    @ExceptionHandler
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    public ErrorResponse handleNotFound(final NotFoundException exception) {
//        return new ErrorResponse(exception.getMessage());
//    }
//
//    //Docker version
//    @ExceptionHandler(HttpClientErrorException.BadRequest.class)
//    public ResponseEntity<ErrorResponse> handleHttpClientErrorValidation(final HttpClientErrorException.BadRequest exception) {
//        return new ResponseEntity<>(new ErrorResponse(exception.getResponseBodyAsString()),
//                HttpStatus.BAD_REQUEST);
//    }
//    //Local version
//    @ExceptionHandler
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ErrorResponse handleValidation(final ValidationException exception) {
//        return new ErrorResponse(exception.getMessage());
//    }
//
////    @ExceptionHandler
////    @ResponseStatus(HttpStatus.BAD_REQUEST)
////    public ErrorResponse handleValidation(final ValidationException exception) {
////        return new ErrorResponse(exception.getMessage());
////    }
//
//    /**
//     * TO DO
//     * Complete Exception
//     */
//
//    @ExceptionHandler
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public ErrorResponse handleHttpClientError(final HttpClientErrorException exception) {
//        return new ErrorResponse(exception.getResponseBodyAsString());
//    }
//
////    @ExceptionHandler
////    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
////    public ErrorResponse handleInternalServerError(final InternalServerException exception) {
////        return new ErrorResponse(exception.getMessage());
////    }
//
//    //Docker version
//    @ExceptionHandler(HttpClientErrorException.Forbidden.class)
//    public ResponseEntity<ErrorResponse> handleHttpClientErrorAccessDenied(final HttpClientErrorException.Forbidden exception) {
//        return new ResponseEntity<>(new ErrorResponse(exception.getResponseBodyAsString()),
//                HttpStatus.FORBIDDEN);
//    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(final NotFoundException exception) {
        return new ErrorResponse(exception.getMessage());
    }

//    @ExceptionHandler(HttpClientErrorException.NotFound.class)
//    public ResponseEntity<ErrorResponse> handleHttpClientErrorNotFound(final HttpClientErrorException.NotFound exception) {
//        String errorMessage = exception.getResponseBodyAsString();
//        return new ResponseEntity<>(new ErrorResponse(errorMessage), HttpStatus.NOT_FOUND);
//    }

    @ExceptionHandler(HttpClientErrorException.NotFound.class)
    public ErrorResponse handleHttpClientErrorNotFound(final HttpClientErrorException.NotFound exception) {
        String errorMessage = exception.getResponseBodyAsString();
        return new ErrorResponse(exception.getResponseBodyAsString());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(final ValidationException exception) {
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternalServerError(final InternalServerException exception) {
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleAccessDenied(final AccessDeniedException exception) {
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleHttpClientError(final HttpClientErrorException exception) {
        return new ErrorResponse(exception.getResponseBodyAsString());
    }

}

