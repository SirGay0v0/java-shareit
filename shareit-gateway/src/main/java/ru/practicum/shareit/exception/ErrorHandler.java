package ru.practicum.shareit.exception;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

@RestControllerAdvice
public class ErrorHandler {

//    //Docker version
//    @ExceptionHandler(HttpClientErrorException.NotFound.class)
//    public ResponseEntity<ErrorResponse> handleHttpClientErrorNotFound(final HttpClientErrorException.NotFound exception) {
//        return new ResponseEntity<>(new ErrorResponse(exception.getResponseBodyAsString()),
//                HttpStatus.NOT_FOUND);
//    }
//
//    //Docker version
//    @ExceptionHandler(HttpClientErrorException.BadRequest.class)
//    public ResponseEntity<ErrorResponse> handleHttpClientErrorValidation(final HttpClientErrorException.BadRequest exception) {
//        return new ResponseEntity<>(new ErrorResponse(exception.getResponseBodyAsString()),
//                HttpStatus.BAD_REQUEST);
//    }
//
//    //Local version
//    @ExceptionHandler
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ErrorResponse handleValidation(final ValidationException exception) {
//        return new ErrorResponse(exception.getMessage());
//    }
//
//    /**
//     * TO DO
//     * compete Exception
//     */
////    Docker version
////    @ExceptionHandler(HttpClientErrorException.)
////    public ResponseEntity<ErrorResponse> handleHttpClientErrorValidation(final HttpClientErrorException.BadRequest exception) {
////        return new ResponseEntity<>(new ErrorResponse(exception.getResponseBodyAsString()),
////                HttpStatus.BAD_REQUEST);
////    }
//
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
//
//

    private final ObjectMapper objectMapper;

    public ErrorHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    //Local exceptions

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(final NotFoundException exception) {
        return new ErrorResponse(exception.getMessage());
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

    //Exceptions from shareIt-server

//    @ExceptionHandler(HttpClientErrorException.class)
//    public ErrorResponse handleHttpClientErrorInternalServerException(ErrorResponse response) {
//        return response;
//    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ErrorResponse> handleHttpClientErrorException(HttpClientErrorException exception) {
        ErrorResponse errorResponse = parseErrorResponse(exception.getResponseBodyAsString());
        return new ResponseEntity<>(errorResponse, exception.getStatusCode());
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<ErrorResponse> handleHttpServerErrorException(HttpServerErrorException exception) {
        ErrorResponse errorResponse = parseErrorResponse(exception.getMessage());
        return new ResponseEntity<>(errorResponse, exception.getStatusCode());
    }

    private ErrorResponse parseErrorResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            return new ErrorResponse(root.get("error").asText());
        } catch (Exception e) {
            return new ErrorResponse("Unknown error");
        }
    }
}

